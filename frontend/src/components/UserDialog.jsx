import { useState } from 'react';
import Icon from './Icon.jsx';

export default function UserDialog({ initialValue, onClose, onSubmit, loading, error }) {
  const [userId, setUserId] = useState(initialValue || '');
  return <div className="modal-backdrop" onMouseDown={(event) => event.target === event.currentTarget && onClose()}>
    <section className="user-dialog" role="dialog" aria-modal="true" aria-labelledby="user-dialog-title">
      <button className="modal-close" onClick={onClose} aria-label="Close"><Icon name="close"/></button>
      <span className="dialog-icon"><Icon name="spark" size={22}/></span><div className="eyebrow">MAKE IT PERSONAL</div><h2 id="user-dialog-title">Your player ID</h2>
      <p>Use your existing GameSense user ID to get recommendations based on games you’ve reviewed.</p>
      <form onSubmit={(event) => { event.preventDefault(); onSubmit(userId.trim()); }}>
        <label className="field-label" htmlFor="player-id">User ID</label><input id="player-id" className="text-field" value={userId} onChange={(event) => setUserId(event.target.value)} placeholder="e.g. AG3D…" autoFocus required/>
        {error && <div className="form-error">{error}</div>}<button className="button-primary dialog-submit" type="submit" disabled={loading || !userId.trim()}>{loading ? 'Connecting…' : 'Continue'}<Icon name="arrow" size={17}/></button>
      </form>
      <span className="dialog-footnote">Your ID is used only to request your game recommendations and reviews.</span>
    </section>
  </div>;
}
