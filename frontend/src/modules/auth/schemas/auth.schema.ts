import { z } from 'zod';

export const loginSchema = z.object({
  email: z
    .email('common.forms.fields.email.errors.invalid')
    .min(1, 'common.forms.fields.errors.required'),
  password: z.string().min(8, 'common.forms.fields.password.errors.min_length'),
});

export const forgotSchema = z.object({
  email: z
    .email('common.forms.fields.email.errors.invalid')
    .min(1, 'common.forms.fields.errors.required'),
});

export type LoginData = z.infer<typeof loginSchema>;
