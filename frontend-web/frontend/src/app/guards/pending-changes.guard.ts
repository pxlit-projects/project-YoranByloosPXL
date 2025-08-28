import { CanDeactivateFn } from '@angular/router';
import { FormGroup } from '@angular/forms';

type HasForm = { form: FormGroup; saving: boolean };

export const pendingChangesGuard: CanDeactivateFn<HasForm> = (component) => {
  if (component.saving) return true;
  if (component.form?.dirty) {
    return window.confirm('Je hebt niet-opgeslagen wijzigingen. Toch verlaten?');
  }
  return true;
};
