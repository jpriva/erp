import { createTheme } from '@mui/material';
import { baseOptions } from './base.options';
import { generateShadows } from './shadows';
import { KANAGAWA_COLORS_DRAGON } from './kanagawaDragon.palette.ts';

export const darkTheme = createTheme({
  ...baseOptions,
  palette: {
    mode: 'dark',
    primary: {
      main: KANAGAWA_COLORS_DRAGON.normalBlue,
      light: KANAGAWA_COLORS_DRAGON.brightBlue,
    },
    secondary: {
      main: KANAGAWA_COLORS_DRAGON.normalMagenta,
    },
    error: {
      main: KANAGAWA_COLORS_DRAGON.normalRed,
    },
    background: {
      default: KANAGAWA_COLORS_DRAGON.primaryBackground,
      paper: KANAGAWA_COLORS_DRAGON.normalBlack,
    },
    text: {
      primary: KANAGAWA_COLORS_DRAGON.primaryForeground,
      secondary: KANAGAWA_COLORS_DRAGON.normalWhite,
    },
    divider: KANAGAWA_COLORS_DRAGON.brightBlack,
  },
  shadows: generateShadows('dark'),
  components: {
    ...baseOptions.components,
    MuiButton: {
      ...baseOptions.components?.MuiButton,
      styleOverrides: {
        ...baseOptions.components?.MuiButton?.styleOverrides,
      },
    },
  },
});
