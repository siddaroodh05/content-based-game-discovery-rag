import csv
import json


METADATA_CSV = "./data/games_metadata.csv"
METADATA_JSONL = "./data/meta_Video_Games.jsonl"


print("Reading image URLs from metadata JSONL...")

image_lookup = {}

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

        if not parent_asin:
            continue

        images = product.get("images", [])

        thumb = ""

        if isinstance(images, list) and images:

            first_image = images[0]

            if isinstance(first_image, dict):
                thumb = str(first_image.get("large", "")).strip()

        image_lookup[parent_asin] = thumb


print(f"Games with image entries: {len(image_lookup)}")


print()
print("Reading existing games_metadata.csv...")

rows = []

with open(METADATA_CSV, "r", encoding="utf-8", newline="") as f:

    reader = csv.DictReader(f)

    fieldnames = reader.fieldnames

    for row in reader:

        parent_asin = row["parent_asin"]

        # Add the image URL using parent_asin as the common ID.
        row["thumb"] = image_lookup.get(parent_asin, "")

        rows.append(row)


# Add thumb as the final column if it does not already exist.
if "thumb" not in fieldnames:
    fieldnames.append("thumb")


print()
print("Updating games_metadata.csv...")

with open(METADATA_CSV, "w", encoding="utf-8", newline="") as f:

    writer = csv.DictWriter(f, fieldnames=fieldnames)

    writer.writeheader()
    writer.writerows(rows)


matched = sum(1 for row in rows if row["thumb"])
missing = len(rows) - matched


print()
print("=" * 60)
print("THUMBNAIL UPDATE COMPLETE")
print("=" * 60)
print(f"Games in CSV:     {len(rows)}")
print(f"Images matched:   {matched}")
print(f"Images missing:   {missing}")
print()
print("Updated file:")
print(f"  {METADATA_CSV}")