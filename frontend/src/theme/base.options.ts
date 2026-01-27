import type { ThemeOptions } from '@mui/material';

export const baseOptions: ThemeOptions = {
  shape: { borderRadius: 8 },
  typography: {
    fontFamily: '"Inter","Roboto", "Arial", sans-serif',
    h1: { fontWeight: 700, fontSize: '2.5rem' },
    h4: { fontWeight: 600, letterSpacing: '0.02em' },
    button: {
      textTransform: 'none',
      fontWeight: 600,
    },
    body1: { fontSize: '0.9375rem' },
  },
  components: {
    MuiButton: {
      defaultProps: {
        disableElevation: true,
      },
      styleOverrides: {
        root: {
          padding: '8px 20px',
        },
      },
    },
    MuiPaper: {
      defaultProps: {
        variant: 'outlined',
      },
      styleOverrides: {
        root: {
          backgroundImage: 'none',
        },
      },
    },
    MuiTableCell: {
      styleOverrides: {
        root: {
          padding: '12px 16px',
        },
      },
    },
  },
};
