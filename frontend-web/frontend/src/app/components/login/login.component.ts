import { Component, inject } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { NgIf } from '@angular/common';
import { AuthService } from '../../services/auth/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, NgIf],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private router = inject(Router);
  private auth = inject(AuthService);

  form = this.fb.group({
    username: ['', [Validators.required]],
    isEditor: [false]
  });

  submitting = false;
  errorMsg = '';

  async submit() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.submitting = true;
    this.errorMsg = '';
    const { username, isEditor } = this.form.getRawValue();

    try {
      this.auth.login(username!, !!isEditor);
      await this.router.navigate(['/']);
      window.location.reload()
    } catch (e) {
      this.errorMsg = 'Er ging iets mis bij het inloggen.';
    } finally {
      this.submitting = false;
    }
  }
}
