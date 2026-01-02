import React from 'react';
import styles from './FeatureBadge.module.css';

const badgeConfig = {
    new: {
        label: 'New',
        icon: '✨',
        className: 'badgeNew'
    },
    updated: {
        label: 'Updated',
        icon: '🔄',
        className: 'badgeUpdated'
    },
    experimental: {
        label: 'Experimental',
        icon: '🧪',
        className: 'badgeExperimental'
    },
    popular: {
        label: 'Popular',
        icon: '🔥',
        className: 'badgePopular'
    },
    recommended: {
        label: 'Recommended',
        icon: '⭐',
        className: 'badgeRecommended'
    },
    beta: {
        label: 'Beta',
        icon: '🚧',
        className: 'badgeBeta'
    }
};

export default function FeatureBadge({ type, text }) {
    const config = badgeConfig[type] || badgeConfig.new;
    const displayText = text || config.label;

    return (
        <span className={`${styles.badge} ${styles[config.className]}`}>
            <span className={styles.icon}>{config.icon}</span>
            {displayText}
        </span>
    );
}

export function FeatureBadgeGroup({ badges }) {
    if (!badges || badges.length === 0) return null;

    return (
        <div className={styles.badgeGroup}>
            {badges.map((badge, i) => (
                <FeatureBadge
                    key={i}
                    type={typeof badge === 'string' ? badge : badge.type}
                    text={typeof badge === 'string' ? null : badge.text}
                />
            ))}
        </div>
    );
}
