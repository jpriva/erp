import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';
import LanguageDetector from 'i18next-browser-languagedetector';
import { enCommon, esCommon } from './index';
import { enAuth, esAuth } from '@modules/auth/locales';

i18n
  .use(LanguageDetector)
  .use(initReactI18next)
  .init({
    resources: {
      en: { common: enCommon, auth: enAuth },
      es: { common: esCommon, auth: esAuth },
    },
    fallbackLng: 'es',
    ns: ['common', 'auth'],
    defaultNS: 'common',
    debug: true,
    interpolation: {
      escapeValue: false,
    },
  })
  .then(() => {
    console.log('i18n initialized');
  })
  .catch((error) => {
    console.error('Error initializing i18n:', error);
  });
