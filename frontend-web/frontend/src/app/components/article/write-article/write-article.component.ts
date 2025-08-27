import { Component, inject } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { NgIf } from '@angular/common';
import { Router } from '@angular/router';
import { PostService } from '../../../services/post/post.service';
import { AuthService } from '../../../services/auth/auth.service';

@Component({
  selector: 'app-write-article',
  standalone: true,
  imports: [ReactiveFormsModule, NgIf],
  templateUrl: './write-article.component.html',
  styleUrls: ['./write-article.component.css']
})
export class WriteArticleComponent {
  private fb = inject(FormBuilder);
  private posts = inject(PostService);
  private router = inject(Router);
  auth = inject(AuthService);

  form = this.fb.group({
    title: ['', [Validators.required, Validators.maxLength(140)]],
    description: ['', [Validators.required, Validators.maxLength(5000)]]
  });

  saving = false;
  errorMsg = '';
  successMsg = '';

  async saveDraft() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.saving = true;
    this.errorMsg = '';
    this.successMsg = '';

    const { title, description } = this.form.getRawValue();

    try {
      await this.posts.createDraft(title!, description!);
      this.successMsg = 'Draft opgeslagen.';
      this.form.markAsPristine();
      // eventueel naar "Mijn drafts"
      // await this.router.navigate(['/admin/drafts']);
    } catch (e) {
      this.errorMsg = 'Opslaan mislukt. Probeer later opnieuw.';
    } finally {
      this.saving = false;
    }
  }
}
