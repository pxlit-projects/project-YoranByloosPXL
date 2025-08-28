import { Component, OnInit, inject } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule, NgIf, NgFor, DatePipe } from '@angular/common';
import { PostService } from '../../../services/post/post.service';
import { Post } from '../../../models/post.model';
import { ReviewService } from '../../../services/review/review.service';
import { Review } from '../../../models/review.model';
import { firstValueFrom } from 'rxjs';

@Component({
  selector: 'app-update-article',
  standalone: true,
  imports: [ReactiveFormsModule, NgIf, NgFor, DatePipe],
  templateUrl: './update-article.component.html',
  styleUrls: ['./update-article.component.css'],
})
export class UpdateArticleComponent implements OnInit {
  private fb = inject(FormBuilder);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private postService = inject(PostService);
  private reviewService = inject(ReviewService);

  form = this.fb.group({
    title: ['', Validators.required],
    description: ['', Validators.required],
  });

  id!: number;
  loading = true;
  saving = false;
  error = '';

  reviews: Review[] = [];

  async ngOnInit() {
    this.id = Number(this.route.snapshot.paramMap.get('id'));
    const fromState = history.state?.post as Post | undefined;

    try {
      const post: Post = fromState ?? await this.postService.getById(this.id);
      this.form.patchValue({
        title: post.title ?? '',
        description: post.content ?? ''
      });

      try {
        this.reviews = await firstValueFrom(this.reviewService.getByPostId(this.id));
      } catch { this.reviews = []; }

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
