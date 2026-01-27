import { type ReactNode, useMemo, useState } from 'react';
import { darkTheme, lightTheme } from '@/theme';
import { CssBaseline, ThemeProvider } from '@mui/material';
import { ThemeContext, type ThemeType } from '@/context/ThemeContext.ts';

export const ThemeContextProvider = ({ children }: { children: ReactNode }) => {
  const [themeName, setThemeName] = useState<ThemeType>(() => {
    return (localStorage.getItem('theme') as ThemeType) || 'dark';
  });
  const theme = useMemo(() => {
    localStorage.setItem('theme', themeName);
    return themeName === 'light' ? lightTheme : darkTheme;
  }, [themeName]);
  return (
    <ThemeContext.Provider value={{ themeName, setThemeName }}>
      <ThemeProvider theme={theme}>
        <CssBaseline />
        {children}
      </ThemeProvider>
    </ThemeContext.Provider>
  );
};
