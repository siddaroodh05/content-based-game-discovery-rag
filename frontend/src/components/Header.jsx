import Icon from './Icon.jsx';

export default function Header({ query, setQuery, onSearch, onMenu, loading, userId, onProfile }) {
  return <header className="topbar">
    <button className="icon-button menu-button" onClick={onMenu} aria-label="Open navigation"><Icon name="menu"/></button>
    <form className="search-box" onSubmit={onSearch} role="search">
      <Icon name="search" size={19}/>
      <input aria-label="Search for a game" value={query} onChange={(event) => setQuery(event.target.value)} placeholder="Search a game, mood, or genre…" />
      {query && <button className="search-clear" type="button" aria-label="Clear search" onClick={() => setQuery('')}><Icon name="close" size={16}/></button>}
      <button className="search-submit" type="submit" disabled={loading}>{loading ? 'Searching…' : 'Explore'}<Icon name="arrow" size={16}/></button>
    </form>
    <button className="top-profile" onClick={onProfile} aria-label={userId ? `Player ${userId}` : 'Sign in'}><span className="avatar avatar--small">{userId ? userId.slice(0, 1).toUpperCase() : 'G'}</span><span className="top-profile-label">{userId || 'Sign in'}</span></button>
  </header>;
}
