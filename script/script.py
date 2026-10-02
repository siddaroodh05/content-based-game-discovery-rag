import csv
import json
import random
from collections import defaultdict


CORE_CSV = "Video_Games.csv"
REVIEWS_JSONL = "Video_Games.jsonl"
METADATA_JSONL = "meta_Video_Games.jsonl"

HISTORY_OUTPUT = "history.csv"
METADATA_OUTPUT = "games_metadata.csv"

NUM_USERS = 2500
HISTORY_PER_USER = 8

RANDOM_SEED = 42


def flatten_list_field(value, sep=" | "):
    if not value:
        return ""

    if isinstance(value, list):
        cleaned = [str(v).strip() for v in value if str(v).strip()]
        return sep.join(cleaned)

    return str(value).strip()


print("Reading game metadata...")

valid_metadata = {}
dropped_incomplete = 0

with open(METADATA_JSONL, "r", encoding="utf-8") as f:

    for line in f:

        line = line.strip()

        if not line:
            continue

        try:
            product = json.loads(line)
        except json.JSONDecodeError:
            continue

        parent_asin = product.get("parent_asin")
        main_category = product.get("main_category")

        if not (parent_asin and main_category == "Video Games"):
            continue

        title = str(product.get("title", "")).strip()
        categories = flatten_list_field(product.get("categories", []))
        features = flatten_list_field(product.get("features", []))
        description = flatten_list_field(product.get("description", []))

        # Completeness check: all four must be non-empty.
        if not (title and categories and features and description):
            dropped_incomplete += 1
            continue

        # Get the large image URL for UI display.
        thumb = ""

        images = product.get("images", [])

        if isinstance(images, list) and images:

            first_image = images[0]

            if isinstance(first_image, dict):
                thumb = str(first_image.get("large", "")).strip()

        valid_metadata[parent_asin] = {
            "parent_asin": parent_asin,
            "title": title,
            "main_category": main_category,
            "categories": categories,
            "features": features,
            "description": description,
            "average_rating": product.get("average_rating", ""),
            "rating_number": product.get("rating_number", ""),
            "price": product.get("price", ""),
            "store": product.get("store", ""),
            "thumb": thumb
        }


print(f"Valid + complete Video Game metadata: {len(valid_metadata)}")
print(f"Dropped for missing title/categories/features/description: {dropped_incomplete}")


print()
print("Reading review JSONL (building reviewed-interaction lookup)...")

review_text_lookup = {}

with open(REVIEWS_JSONL, "r", encoding="utf-8") as f:

    for line in f:

        line = line.strip()

        if not line:
            continue

        try:
            review = json.loads(line)
        except json.JSONDecodeError:
            continue

        user_id = review.get("user_id")
        parent_asin = review.get("parent_asin")
        timestamp = review.get("timestamp")
        text = str(review.get("text", "")).strip()

        if user_id is None or parent_asin is None or timestamp is None:
            continue

        # Must be a game that passed the metadata completeness filter
        if parent_asin not in valid_metadata:
            continue

        # Must actually have review text
        if not text:
            continue

        review_text_lookup[(user_id, parent_asin, timestamp)] = text


print(f"Interactions with usable (non-empty) review text: {len(review_text_lookup)}")


print()
print("Reading 5-core dataset...")

user_interactions = defaultdict(list)

with open(CORE_CSV, "r", encoding="utf-8", newline="") as f:

    reader = csv.DictReader(f)

    for row in reader:

        user_id = row["user_id"]
        parent_asin = row["parent_asin"]
        timestamp = int(row["timestamp"])

        if parent_asin not in valid_metadata:
            continue

        key = (user_id, parent_asin, timestamp)

        if key not in review_text_lookup:
            continue

        user_interactions[user_id].append({
            "user_id": user_id,
            "parent_asin": parent_asin,
            "rating": row["rating"],
            "timestamp": timestamp,
            "review_text": review_text_lookup[key]
        })


print(f"Users with valid + reviewed Video Game interactions: {len(user_interactions)}")


eligible_users = [
    user_id
    for user_id, interactions in user_interactions.items()
    if len(interactions) >= HISTORY_PER_USER
]

print(
    f"Users with at least {HISTORY_PER_USER} "
    f"qualifying interactions: {len(eligible_users)}"
)

effective_num_users = min(NUM_USERS, len(eligible_users))

if effective_num_users < NUM_USERS:
    print(
        f"NOTE: Only {len(eligible_users)} eligible users available. "
        f"Shrinking NUM_USERS from {NUM_USERS} to {effective_num_users}."
    )


random.seed(RANDOM_SEED)

selected_users = random.sample(eligible_users, effective_num_users)

print(f"Selected users: {len(selected_users)}")


history_rows = []

selected_game_ids = set()

for user_id in selected_users:

    interactions = user_interactions[user_id]

    # Oldest -> newest
    interactions.sort(key=lambda x: x["timestamp"])

    # Take HISTORY_PER_USER most recent qualifying interactions
    selected_interactions = interactions[-HISTORY_PER_USER:]

    history_rows.extend(selected_interactions)

    for interaction in selected_interactions:
        selected_game_ids.add(interaction["parent_asin"])


print()
print("Interaction extraction complete.")
print(f"History rows: {len(history_rows)}")
print(f"Unique games: {len(selected_game_ids)}")


print()
print("Writing history.csv...")

output_fields = ["user_id", "parent_asin", "rating", "review_text"]


def trim(rows):
    return [
        {
            "user_id": r["user_id"],
            "parent_asin": r["parent_asin"],
            "rating": r["rating"],
            "review_text": r["review_text"]
        }
        for r in rows
    ]


with open(HISTORY_OUTPUT, "w", encoding="utf-8", newline="") as f:
    writer = csv.DictWriter(f, fieldnames=output_fields)
    writer.writeheader()
    writer.writerows(trim(history_rows))


print()
print("Writing games_metadata.csv...")

metadata_fields = [
    "parent_asin",
    "title",
    "main_category",
    "categories",
    "features",
    "description",
    "average_rating",
    "rating_number",
    "price",
    "store",
    "thumb"
]

selected_metadata_rows = [
    valid_metadata[parent_asin]
    for parent_asin in selected_game_ids
    if parent_asin in valid_metadata
]

with open(METADATA_OUTPUT, "w", encoding="utf-8", newline="") as f:
    writer = csv.DictWriter(f, fieldnames=metadata_fields)
    writer.writeheader()
    writer.writerows(selected_metadata_rows)


print()
print("=" * 60)
print("EXTRACTION COMPLETE")
print("=" * 60)
print(f"Valid + complete Video Games in metadata: {len(valid_metadata)}")
print(f"Eligible users (>= {HISTORY_PER_USER} qualifying interactions): {len(eligible_users)}")
print(f"Selected users:                {len(selected_users)}")
print(f"History rows:                  {len(history_rows)}")
print(f"Unique selected games:         {len(selected_game_ids)}")
print(f"Metadata rows:                 {len(selected_metadata_rows)}")
print()
print("Files created:")
print(f"  {HISTORY_OUTPUT}")
print(f"  {METADATA_OUTPUT}")