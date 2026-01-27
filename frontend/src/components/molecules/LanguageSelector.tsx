import { FormControl, MenuItem, Select } from '@mui/material';
import { useAppLanguage } from '@/hooks';

export const LanguageSelector = () => {
  const { language, changeLanguage } = useAppLanguage();

  return (
    <FormControl size="small" sx={{ minWidth: 120 }}>
      <Select value={language} onChange={(e) => changeLanguage(e.target.value)} variant="outlined">
        <MenuItem value="es">Español</MenuItem>
        <MenuItem value="en">English</MenuItem>
      </Select>
    </FormControl>
  );
};
