import { createContext } from 'react';

interface LanguageContextType {
  language: string;
  changeLanguage: (lng: string) => void;
}

export const LanguageContext = createContext<LanguageContextType | undefined>(undefined);
