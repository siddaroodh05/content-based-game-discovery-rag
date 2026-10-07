import { useEffect, useState } from 'react';
import { api } from '../services/api.js';
import Icon from './Icon.jsx';
import { ratingLabel } from '../utils/format.js';

export default function GameDetails({ game, onClose, saved, onToggleSaved }) {
  const [details, setDetails] = useState(null);
  const [error, setError] = useState('');
  useEffect(() => {
    let alive = true;
    if (game.parentAsin) api.gameMetadata(game.parentAsin).then((data) => alive && setDetails(data)).catch((reason) => alive && setError(reason.message));
    return () => { alive = false; };
  }, [game.parentAsin]);
  useEffect(() => {
    const handleKey = (event) => event.key === 'Escape' && onClose();
    window.addEventListener('keydown', handleKey);
    return () => window.removeEventListener('keydown', handleKey);
  }, [onClose]);
  const image = game.thumbnail || details?.thumbnail;
  return <div className="modal-backdrop" onMouseDown={(event) => event.target === event.currentTarget && onClose()}>
    <section className="detail-modal" role="dialog" aria-modal="true" aria-labelledby="detail-title">
      <button className="modal-close" onClick={onClose} aria-label="Close details"><Icon name="close"/></button>
      <div className="detail-artwork">{image ? <img src={image} alt=""/> : <div className="detail-artwork-placeholder"><Icon name="spark" size={44}/></div>}<span className="artwork-vignette"/></div>
      <div className="detail-body">
        <div className="eyebrow">GAME DETAILS</div><h2 id="detail-title">{game.title || 'Untitled game'}</h2>
        <div className="detail-meta"><span><Icon name="star" size={15}/>{ratingLabel(game.averageRating ?? details?.averageRating)}</span><span>{details?.categories || game.categories || 'Explore this title'}</span></div>
        <p>{details?.features || game.features || (error ? 'Game details are not available right now.' : 'Loading details…')}</p>
        <button className={`button-primary ${saved ? 'button-primary--saved' : ''}`} onClick={() => onToggleSaved(game)}><Icon name="bookmark" size={17}/>{saved ? 'Saved to your library' : 'Add to your library'}</button>
      </div>
    </section>
  </div>;
}
