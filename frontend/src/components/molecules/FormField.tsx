import type { TextFieldProps } from '@mui/material';
import {
  type Control,
  Controller,
  type FieldError,
  type FieldValues,
  type Path,
} from 'react-hook-form';
import { TextFieldCustom } from '../atoms';
import { useTranslation } from 'react-i18next';

interface FormFieldProps<T extends FieldValues> extends Omit<TextFieldProps, 'name' | 'error'> {
  name: Path<T>;
  control: Control<T>;
  error?: FieldError;
}

export const FormField = <T extends FieldValues>({
  name,
  control,
  error,
  label,
  ...props
}: FormFieldProps<T>) => {
  const { t } = useTranslation();

  return (
    <Controller
      name={name}
      control={control}
      render={({ field }) => (
        <TextFieldCustom
          {...field}
          {...props}
          label={label}
          error={!!error}
          helperText={error ? t(error.message || '') : props.helperText}
        />
      )}
    />
  );
};
