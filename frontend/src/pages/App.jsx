import { useCallback, useEffect, useMemo, useState } from 'react';
import Sidebar from '../components/Sidebar.jsx';
import Header from '../components/Header.jsx';
import Icon from '../components/Icon.jsx';
import GameGrid from '../components/GameGrid.jsx';
import GameDetails from '../components/GameDetails.jsx';
import UserDialog from '../components/UserDialog.jsx';
import { genres } from '../data/genres.js';
import { api, unwrapPage } from '../services/api.js';

export default function App() {
  const [active, setActive] = useState('discover');
  const [genre, setGenre] = useState('All games');
  const [query, setQuery] = useState('');
  const [games, setGames] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalGames, setTotalGames] = useState(null);
  const [searchMode, setSearchMode] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [saved, setSaved] = useState(() => JSON.parse(localStorage.getItem('gamesense-saved') || '[]'));
  const [userId, setUserId] = useState(() => localStorage.getItem('gamesense-user-id') || '');
  const [detail, setDetail] = useState(null);
  const [dialog, setDialog] = useState(false);
  const [dialogError, setDialogError] = useState('');
  const [dialogLoading, setDialogLoading] = useState(false);
  const [recommendationMode, setRecommendationMode] = useState('recent');
  const [recommendationQuery, setRecommendationQuery] = useState('');
  const [mobileNav, setMobileNav] = useState(false);
  const [reviews, setReviews] = useState([]);
  const [reviewsPage, setReviewsPage] = useState(0);
  const [reviewsTotalPages, setReviewsTotalPages] = useState(0);
  const [reviewsLoading, setReviewsLoading] = useState(false);
  const [reviewsError, setReviewsError] = useState('');

  const loadReviews = useCallback(async (nextPage = 0, id = userId) => {
    if (!id) { setReviews([]); return; }
    setReviewsLoading(true); setReviewsError('');
    try {
      const result = unwrapPage(await api.reviews(id, nextPage));
      setReviews(result.items); setReviewsPage(nextPage); setReviewsTotalPages(result.totalPages);
    } catch (reason) { setReviews([]); setReviewsError(reason.message); }
    finally { setReviewsLoading(false); }
  }, [userId]);

  const loadGames = useCallback(async (nextPage = 0) => {
    setLoading(true); setError('');
    try {
      const result = unwrapPage(await api.games(nextPage));
      setGames(result.items); setPage(nextPage); setTotalPages(result.totalPages); setTotalGames(result.totalElements); setSearchMode(false);
    } catch (reason) { setGames([]); setError(reason.message); }
    finally { setLoading(false); }
  }, []);

  useEffect(() => { loadGames(); }, [loadGames]);

  const showRecommendations = async (mode = recommendationMode) => {
    if (!userId) { setDialog(true); return; }
    setLoading(true); setError(''); setActive('for-you'); setRecommendationMode(mode);
    try {
      const result = await api.recommendations(userId, mode === 'recent' ? 'recent' : 'top_rated');
      setGames(result?.games || []); setRecommendationQuery(result?.query || ''); setSearchMode(false);
    } catch (reason) { setGames([]); setError(reason.message); }
    finally { setLoading(false); }
  };

  const handleSearch = async (event) => {
    event?.preventDefault();
    if (!query.trim()) { loadGames(); return; }
    setLoading(true); setError(''); setActive('discover'); setGenre('All games');
    try { setGames(await api.customSearch(query.trim())); setSearchMode(true); setTotalPages(0); }
    catch (reason) { setGames([]); setError(reason.message); }
    finally { setLoading(false); }
  };

  const handleGenre = async (selectedGenre) => {
    setGenre(selectedGenre);
    if (selectedGenre === 'All games') { setQuery(''); loadGames(); return; }
    setLoading(true); setError(''); setActive('discover'); setQuery('');
    try { setGames(await api.customSearch(`${selectedGenre} games`)); setSearchMode(true); setTotalPages(0); }
    catch (reason) { setGames([]); setError(reason.message); }
    finally { setLoading(false); }
  };

  const handleNavigate = (destination) => {
    setActive(destination);
    if (destination === 'discover') { setQuery(''); setGenre('All games'); loadGames(); }
    if (destination === 'for-you') showRecommendations();
    if (destination === 'reviews') loadReviews(0);
  };

  const toggleSaved = (game) => {
    const key = game.parentAsin;
    if (!key) return;
    const next = saved.some((item) => item.parentAsin === key) ? saved.filter((item) => item.parentAsin !== key) : [...saved, game];
    setSaved(next); localStorage.setItem('gamesense-saved', JSON.stringify(next));
  };

  const connectUser = async (id) => {
    if (!id) return;
    setDialogLoading(true); setDialogError('');
    try {
      const response = await api.login(id);
      const confirmedId = response?.userId || id;
      setUserId(confirmedId); localStorage.setItem('gamesense-user-id', confirmedId); setDialog(false);
      if (active === 'reviews') loadReviews(0, confirmedId);
    } catch (reason) { setDialogError(reason.message); }
    finally { setDialogLoading(false); }
  };

  const visibleGames = useMemo(() => {
    if (active === 'saved') return saved;
    if (genre === 'All games' || active !== 'discover' || searchMode) return games;
    const needle = genre.toLowerCase();
    return games.filter((game) => (game.categories || '').toLowerCase().includes(needle));
  }, [active, games, genre, saved, searchMode]);

  const activeIsSaved = (game) => saved.some((item) => item.parentAsin === game.parentAsin);

  return <div className="app-shell">
    <Sidebar active={active} onNavigate={handleNavigate} open={mobileNav} onClose={() => setMobileNav(false)} userId={userId} onProfile={() => setDialog(true)}/>
    <main className="main-panel">
      <Header query={query} setQuery={setQuery} onSearch={handleSearch} onMenu={() => setMobileNav(true)} loading={loading} userId={userId} onProfile={() => setDialog(true)}/>
      <div className="page-content">
        {active === 'discover' && <>
          {!searchMode && <section className="hero-card">
            <div className="hero-copy"><span className="hero-kicker"><span className="hero-kicker-dot"/>YOUR NEXT ADVENTURE STARTS HERE</span><h1>Find your<br/>next <em>favorite.</em></h1><p>Less scrolling. More playing. Discover the games that feel like they were made for you.</p><button className="hero-cta" onClick={() => showRecommendations()}><Icon name="spark" size={17}/>Find my next game<Icon name="arrow" size={17}/></button></div>
            <div className="hero-visual" aria-hidden="true"><div className="hero-sun"/><div className="hero-ring hero-ring--one"/><div className="hero-ring hero-ring--two"/><div className="hero-planet"/><div className="hero-hill hero-hill--back"/><div className="hero-hill hero-hill--front"/><div className="hero-caption"><span>01 / 03</span><span>ADVENTURE IS OUT THERE</span></div></div>
            <div className="hero-page-count"><span>01</span><i/>03</div>
          </section>}
          <section className="catalog-section">
            <div className="section-heading"><div><span className="eyebrow">THE GOOD STUFF</span><h2>{searchMode ? `Games for “${genre !== 'All games' ? genre : query}”` : 'A world of games'}</h2><p>{searchMode ? 'Picked from your search.' : 'A little inspiration for your next great session.'}</p></div>{!searchMode && <span className="catalog-count">{totalGames == null ? 'Browse the catalog' : `${totalGames.toLocaleString()} games to explore`}<span className="count-dot"/></span>}</div>
            <div className="genre-row" role="tablist" aria-label="Filter games by genre">{genres.map((item) => <button key={item} role="tab" aria-selected={genre === item} className={`genre-pill ${genre === item ? 'genre-pill--active' : ''}`} onClick={() => handleGenre(item)}>{item}</button>)}</div>
            {error && <div className="error-banner" role="alert"><span><strong>We hit a snag.</strong> {error}</span><button className="text-button" onClick={() => searchMode ? handleSearch() : loadGames(page)}>Try again <Icon name="arrow" size={15}/></button></div>}
            {loading ? <div className="loading-grid" aria-label="Loading games">{Array.from({ length: 6 }, (_, index) => <div className="skeleton-card" key={index}><span/><i/><i/></div>)}</div> : <GameGrid games={visibleGames} savedIds={saved.map((item) => item.parentAsin)} onToggleSaved={toggleSaved} onOpen={setDetail}/>}
            {!loading && !searchMode && totalPages > 1 && <div className="pagination"><button className="page-button" disabled={page === 0} onClick={() => loadGames(page - 1)}>← Previous</button><span>Page {page + 1} of {totalPages}</span><button className="page-button" disabled={page + 1 >= totalPages} onClick={() => loadGames(page + 1)}>Next <Icon name="arrow" size={15}/></button></div>}
          </section>
          <footer className="page-footer"><span>GOOD GAMES. BETTER DISCOVERED.</span><span>GAMESENSE <span className="footer-mark">✳</span> MADE FOR PLAYERS</span></footer>
        </>}

        {active === 'for-you' && <section className="inner-page">
          <div className="inner-heading"><span className="eyebrow">YOUR TASTE, YOUR NEXT PLAY</span><h1>Made for <em>you.</em></h1><p>GameSense learns from what you’ve enjoyed and finds your next match.</p></div>
          {!userId ? <div className="connect-card"><span className="dialog-icon"><Icon name="spark" size={24}/></span><h2>Let’s find your next favorite.</h2><p>Connect your player ID to get recommendations from your review history.</p><button className="button-primary" onClick={() => setDialog(true)}>Connect player ID<Icon name="arrow" size={17}/></button></div> : <>
            <div className="recommend-tabs" role="tablist"><button className={recommendationMode === 'recent' ? 'is-selected' : ''} onClick={() => showRecommendations('recent')}>Based on recent reviews</button><button className={recommendationMode === 'top_rated' ? 'is-selected' : ''} onClick={() => showRecommendations('top_rated')}>Based on top rated</button></div>
            {recommendationQuery && <div className="query-insight"><span className="query-insight-icon"><Icon name="spark" size={17}/></span><span><small>WE PICKED UP ON</small><strong>{recommendationQuery}</strong></span></div>}
            {error && <div className="error-banner" role="alert"><span><strong>We hit a snag.</strong> {error}</span><button className="text-button" onClick={() => showRecommendations()}>Try again <Icon name="arrow" size={15}/></button></div>}
            {loading ? <div className="loading-grid" aria-label="Loading recommendations">{Array.from({ length: 3 }, (_, index) => <div className="skeleton-card" key={index}><span/><i/><i/></div>)}</div> : <GameGrid games={games} savedIds={saved.map((item) => item.parentAsin)} onToggleSaved={toggleSaved} onOpen={setDetail}/>}
          </>}
        </section>}

        {active === 'saved' && <section className="inner-page">
          <div className="inner-heading"><span className="eyebrow">YOUR PERSONAL COLLECTION</span><h1>Your <em>library.</em></h1><p>All the games you’ve saved, together in one place.</p></div>
          <div className="library-summary"><span><Icon name="bookmark" size={17}/>{saved.length} {saved.length === 1 ? 'game' : 'games'} saved</span><button className="text-button" onClick={() => handleNavigate('discover')}>Discover more <Icon name="arrow" size={15}/></button></div>
          <GameGrid games={visibleGames} savedIds={saved.map((item) => item.parentAsin)} onToggleSaved={toggleSaved} onOpen={setDetail}/>
        </section>}

        {active === 'reviews' && <section className="inner-page">
          <div className="inner-heading"><span className="eyebrow">YOUR PLAYER HISTORY</span><h1>My <em>reviews.</em></h1><p>The games you’ve rated and the thoughts you’ve shared.</p></div>
          {!userId ? <div className="connect-card"><span className="dialog-icon"><Icon name="star" size={24}/></span><h2>Connect your player ID.</h2><p>Sign in with your player ID to see your review history.</p><button className="button-primary" onClick={() => setDialog(true)}>Connect player ID<Icon name="arrow" size={17}/></button></div> : <>
            {reviewsError && <div className="error-banner" role="alert"><span><strong>We hit a snag.</strong> {reviewsError}</span><button className="text-button" onClick={() => loadReviews(reviewsPage)}>Try again <Icon name="arrow" size={15}/></button></div>}
            {reviewsLoading ? <div className="review-list" aria-label="Loading reviews">{Array.from({ length: 3 }, (_, index) => <div className="review-skeleton" key={index}><span/><i/><i/></div>)}</div> : reviews.length ? <>
              <div className="library-summary"><span><Icon name="star" size={17}/>{reviews.length} {reviews.length === 1 ? 'review' : 'reviews'} on this page</span></div>
              <div className="review-list">{reviews.map((review, index) => <button type="button" className="review-card" key={`${review.parentAsin || review.title}-${index}`} onClick={() => setDetail({ ...review, thumbnail: review.thumb, averageRating: review.rating })} aria-label={`View ${review.title || 'game'} details`}>
                <div className="review-cover">{review.thumb ? <img src={review.thumb} alt="" loading="lazy"/> : <Icon name="spark" size={23}/>}</div>
                <div className="review-copy"><div className="review-title-row"><h2>{review.title || 'Untitled game'}</h2><span className="review-rating"><Icon name="star" size={14}/>{review.rating}</span></div><p>{review.reviewtext || 'No written review.'}</p></div>
              </button>)}</div>
              {reviewsTotalPages > 1 && <div className="pagination"><button className="page-button" disabled={reviewsPage === 0} onClick={() => loadReviews(reviewsPage - 1)}>← Previous</button><span>Page {reviewsPage + 1} of {reviewsTotalPages}</span><button className="page-button" disabled={reviewsPage + 1 >= reviewsTotalPages} onClick={() => loadReviews(reviewsPage + 1)}>Next <Icon name="arrow" size={15}/></button></div>}
            </> : !reviewsError && <div className="empty-state review-empty"><span className="empty-orbit"><Icon name="star" size={28}/></span><h3>No reviews found</h3><p>Your reviews will show up here once they’re available.</p></div>}
          </>}
        </section>}
      </div>
    </main>
    {detail && <GameDetails game={detail} onClose={() => setDetail(null)} saved={activeIsSaved(detail)} onToggleSaved={toggleSaved}/>}
    {dialog && <UserDialog initialValue={userId} onClose={() => { setDialog(false); setDialogError(''); }} onSubmit={connectUser} loading={dialogLoading} error={dialogError}/>}
  </div>;
}
