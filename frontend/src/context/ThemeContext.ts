import { createContext } from 'react';

export type ThemeType = 'light' | 'dark';

interface ThemeContextType {
  themeName: ThemeType;
  setThemeName: (name: ThemeType) => void;
}

export const ThemeContext = createContext<ThemeContextType | undefined>(undefined);
