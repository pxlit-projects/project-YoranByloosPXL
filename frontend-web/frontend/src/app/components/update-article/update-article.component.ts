// src/app/pages/update-article/update-article.component.ts
import { Component, OnInit, inject } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { NgIf } from '@angular/common';
import { PostService } from '../../services/post.service';
import { Post } from '../../models/post.model';

@Component({
  selector: 'app-update-article',
  standalone: true,
  imports: [ReactiveFormsModule, NgIf],
  templateUrl: './update-article.component.html',
  styleUrls: ['./update-article.component.css'],
})
export class UpdateArticleComponent implements OnInit {
  private fb = inject(FormBuilder);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private postService = inject(PostService);

  form = this.fb.group({
    title: ['', Validators.required],
    description: ['', Validators.required],
  });

  id!: number;
  loading = true;
  saving = false;
  error = '';

  async ngOnInit() {
    this.id = Number(this.route.snapshot.paramMap.get('id'));
    const fromState = history.state?.post as Post | undefined;

    try {
      const post: Post = fromState ?? await this.postService.getById(this.id);

      this.form.patchValue({
        title: post.title ?? '',
        // jouw Post heeft 'content', geen 'description'
        description: post.content ?? ''
      });
    } catch {
      this.error = 'Kon post niet laden.';
    } finally {
      this.loading = false;
    }
  }

  async save() {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.saving = true; this.error = '';
    const { title, description } = this.form.getRawValue();
    try {
      await this.postService.updateDraft(this.id, title!, description!);
      this.router.navigate(['/admin/drafts']);
    } catch {
      this.error = 'Opslaan mislukt.';
    } finally {
      this.saving = false;
    }
  }
}
