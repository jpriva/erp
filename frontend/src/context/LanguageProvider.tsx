import { type ReactNode, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { LanguageContext } from './LanguageContext';

export const LanguageProvider = ({ children }: { children: ReactNode }) => {
  const { i18n } = useTranslation();
  const [language, setLanguage] = useState(i18n.language);

  const changeLanguage = (lng: string) => {
    i18n.changeLanguage(lng).then((r) => console.log(r));
    setLanguage(lng);
    localStorage.setItem('lang', lng);
  };

  return (
    <LanguageContext.Provider value={{ language, changeLanguage }}>
      {children}
    </LanguageContext.Provider>
  );
};
