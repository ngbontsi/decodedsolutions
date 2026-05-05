export default function StatCard({
  title,
  value,
  change,
  icon: Icon,
}: {
  title: string;
  value: string | number;
  change?: { value: number; positive: boolean };
  icon: React.ElementType;
}) {
  return (
    <div className="stat-card">
      <div className="stat-header">
        <span className="stat-title">{title}</span>
        <div className="stat-icon">
          <Icon size={20} />
        </div>
      </div>
      <div className="stat-value">{value}</div>
      {change && (
        <div className={`stat-change ${change.positive ? 'positive' : 'negative'}`}>
          {change.positive ? '+' : ''}{change.value}% from last period
        </div>
      )}
    </div>
  );
}
