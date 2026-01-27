import {
  Button,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
} from '@mui/material';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useTranslation } from 'react-i18next';
import { FormField } from '@components/molecules';
import { StyledDialog } from '@components/atoms';
import { forgotSchema } from '../../schemas';

type ForgotData = z.infer<typeof forgotSchema>;

interface Props {
  open: boolean;
  onClose: () => void;
}

export const ForgotPasswordModal = ({ open, onClose }: Props) => {
  const { t } = useTranslation();
  const {
    control,
    handleSubmit,
    formState: { errors },
  } = useForm<ForgotData>({
    resolver: zodResolver(forgotSchema),
  });

  const onSubmit = (data: ForgotData) => {
    console.log('Reset password form submitted:', data.email);
    onClose();
  };

  return (
    <StyledDialog open={open} onClose={onClose} fullWidth maxWidth="xs">
      <DialogTitle fontWeight="bold">{t('auth:forgot.title')}</DialogTitle>

      <form onSubmit={handleSubmit(onSubmit)}>
        <DialogContent>
          <DialogContentText mb={3}>{t('auth:forgot.description')}</DialogContentText>

          <FormField
            name="email"
            label={t('common:forms.fields.email.label')}
            placeholder={t('common:forms.fields.email.placeholder')}
            control={control}
            error={errors.email}
            fullWidth
          />
        </DialogContent>

        <DialogActions sx={{ p: 3 }}>
          <Button onClick={onClose} color="inherit">
            {t('common:forms.buttons.cancel')}
          </Button>
          <Button type="submit" variant="contained">
            {t('auth:forgot.send')}
          </Button>
        </DialogActions>
      </form>
    </StyledDialog>
  );
};
