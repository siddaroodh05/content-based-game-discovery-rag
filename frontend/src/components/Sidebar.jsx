import Icon from './Icon.jsx';

const links = [
  { id: 'discover', label: 'Discover', icon: 'grid' },
  { id: 'for-you', label: 'For you', icon: 'spark' },
  { id: 'saved', label: 'Your library', icon: 'bookmark' },
  { id: 'reviews', label: 'My reviews', icon: 'star' },
];

export default function Sidebar({ active, onNavigate, open, onClose, userId, onProfile }) {
  return <>
    {open && <button className="sidebar-scrim" onClick={onClose} aria-label="Close navigation" />}
    <aside className={`sidebar ${open ? 'sidebar--open' : ''}`}>
      <a className="brand" href="#discover" onClick={() => onNavigate('discover')} aria-label="GameSense home">
        <span className="brand-mark"><Icon name="spark" size={22}/></span><span>game<span className="brand-accent">sense</span></span>
      </a>
      <div className="sidebar-label">YOUR SPACE</div>
      <nav className="side-nav" aria-label="Main navigation">
        {links.map((link) => <button key={link.id} className={`side-link ${active === link.id ? 'is-active' : ''}`} onClick={() => { onNavigate(link.id); onClose(); }}>
          <Icon name={link.icon}/><span>{link.label}</span>{link.id === 'saved' && <span className="nav-count">⌘</span>}
        </button>)}
      </nav>
      <div className="sidebar-bottom">
        <div className="sidebar-note"><span className="online-dot"/><span>Ready to find your next favorite?</span></div>
        <button className="profile-button" onClick={onProfile}>
          <span className="avatar">{userId ? userId.slice(0, 1).toUpperCase() : 'G'}</span>
          <span className="profile-copy"><strong>{userId || 'Guest player'}</strong><small>{userId ? 'Player profile' : 'Sign in with player ID'}</small></span>
          <span className="profile-more">···</span>
        </button>
      </div>
    </aside>
  </>;
}
