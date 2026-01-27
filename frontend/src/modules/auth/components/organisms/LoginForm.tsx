import { ForgotPasswordModal } from '../molecules';
import { Box, Button, CircularProgress, Stack, Typography } from '@mui/material';
import { useTranslation } from 'react-i18next';
import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { type LoginData, loginSchema } from '../../schemas';
import { FormField } from '@components/molecules';

export const LoginForm = () => {
  const { t } = useTranslation();
  const [loading, setLoading] = useState(false);
  const [isForgotOpen, setIsForgotOpen] = useState(false);

  const {
    control,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginData>({
    resolver: zodResolver(loginSchema),
    defaultValues: {
      email: '',
      password: '',
    },
  });

  const onSubmit = async (data: LoginData) => {
    setLoading(true);
    try {
      console.debug('Login data:', data);
      await new Promise((resolve) => setTimeout(resolve, 2000));
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box component="form" onSubmit={handleSubmit(onSubmit)} noValidate>
      <Stack spacing={3}>
        <Box textAlign="center" mb={2}>
          <Typography variant="h4" component="h1" gutterBottom fontWeight="bold">
            {t('auth:login.title')}
          </Typography>
          <Typography variant="body2" color="text.secondary">
            {t('auth:login.subtitle')}
          </Typography>
        </Box>

        <FormField
          name="email"
          label={t('common:forms.fields.email.label')}
          placeholder={t('common:forms.fields.email.placeholder')}
          control={control}
          error={errors.email}
          type="email"
          disabled={loading}
        />

        <FormField
          name="password"
          label={t('common:forms.fields.password.label')}
          placeholder={t('common:forms.fields.password.placeholder')}
          control={control}
          error={errors.password}
          type="password"
          disabled={loading}
        />

        <Button
          type="submit"
          variant="contained"
          size="large"
          fullWidth
          disabled={loading}
          sx={{
            height: '56px',
            mt: 2,
          }}
        >
          {loading ? <CircularProgress size={24} color="inherit" /> : t('auth:login.submit_button')}
        </Button>

        <Button
          variant="text"
          size="small"
          sx={{ color: 'text.secondary', alignSelf: 'center' }}
          onClick={() => setIsForgotOpen(true)}
        >
          {t('auth:login.forgot_password')}
        </Button>
      </Stack>
      <ForgotPasswordModal open={isForgotOpen} onClose={() => setIsForgotOpen(false)} />
    </Box>
  );
};
