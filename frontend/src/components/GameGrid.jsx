import GameCard from './GameCard.jsx';

export default function GameGrid({ games, savedIds, onToggleSaved, onOpen }) {
  if (!games.length) return <div className="empty-state"><span className="empty-orbit">⌕</span><h3>No games found</h3><p>Try another search or browse all games.</p></div>;
  return <div className="game-grid">{games.map((game, index) => <GameCard key={game.parentAsin || `${game.title}-${index}`} game={game} index={index} saved={savedIds.includes(game.parentAsin)} onToggleSaved={onToggleSaved} onOpen={onOpen}/>)}</div>;
}
