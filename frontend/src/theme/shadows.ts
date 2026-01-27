import type { Shadows } from '@mui/material';

const darkShadowColor = 'rgba(0, 0, 0, 0.5)';

const lightShadowColor = 'rgba(84, 84, 100, 0.15)';

const createShadow = (color: string, px: number) => {
  if (px === 0) return 'none';
  return [
    `0px ${px}px ${px * 2}px 0px ${color}`,
    `0px ${px / 2}px ${px}px 0px ${color}`,
    `0px 1px ${px * 3}px 0px ${color}`,
  ].join(', ');
};

export const generateShadows = (mode: 'light' | 'dark'): Shadows => {
  const color = mode === 'light' ? lightShadowColor : darkShadowColor;

  const shadows = Array.from({ length: 25 }, (_, i) => {
    if (i === 0) return 'none';
    return createShadow(color, i);
  });
  return shadows as unknown as Shadows;
};
