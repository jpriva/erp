import { createTheme } from '@mui/material';
import { baseOptions } from './base.options';
import { KANAGAWA_COLORS_LOTUS } from './kanagawaLotus.palette.ts';
import { generateShadows } from './shadows.ts';

export const lightTheme = createTheme({
  ...baseOptions,
  palette: {
    mode: 'light',
    primary: {
      main: KANAGAWA_COLORS_LOTUS.normalBlue,
      dark: KANAGAWA_COLORS_LOTUS.brightBlue,
    },
    secondary: {
      main: KANAGAWA_COLORS_LOTUS.normalMagenta,
    },
    error: {
      main: KANAGAWA_COLORS_LOTUS.normalRed,
    },
    background: {
      default: KANAGAWA_COLORS_LOTUS.primaryBackground,
      paper: '#fffbe3',
    },
    text: {
      primary: KANAGAWA_COLORS_LOTUS.primaryForeground,
      secondary: KANAGAWA_COLORS_LOTUS.normalBlack,
    },
  },
  shadows: generateShadows('light'),
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
