import Icon from './Icon.jsx';
import { ratingLabel } from '../utils/format.js';

const palettes = ['artwork--one', 'artwork--two', 'artwork--three', 'artwork--four', 'artwork--five', 'artwork--six'];

export default function GameCard({ game, index, saved, onToggleSaved, onOpen }) {
  const title = game.title || 'Untitled game';
  const image = game.thumbnail || game.thumb;
  const rating = game.averageRating ?? game.rating;
  return <article className="game-card">
    <div className={`game-artwork ${image ? '' : palettes[index % palettes.length]}`} role="button" tabIndex={0} onClick={() => onOpen(game)} onKeyDown={(event) => event.key === 'Enter' && onOpen(game)} aria-label={`View ${title}`}>
      {image && <img src={image} alt="" loading="lazy" onError={(event) => { event.currentTarget.style.display = 'none'; }}/ >}
      <span className="artwork-vignette"/>
      <span className="artwork-index">{String(index + 1).padStart(2, '0')}</span>
      {rating != null && <span className="rating-badge"><Icon name="star" size={13}/>{ratingLabel(rating)}</span>}
      <button className={`save-button ${saved ? 'save-button--active' : ''}`} onClick={(event) => { event.stopPropagation(); onToggleSaved(game); }} aria-label={saved ? `Remove ${title} from library` : `Save ${title}`}><Icon name="bookmark" size={17}/></button>
      {!image && <span className="artwork-title">{title}</span>}
    </div>
    <button className="game-card-info" onClick={() => onOpen(game)}>
      <span className="game-card-title">{title}</span><span className="game-card-meta">{game.categories || game.parentAsin || 'Discover something new'}</span>
    </button>
  </article>;
}
