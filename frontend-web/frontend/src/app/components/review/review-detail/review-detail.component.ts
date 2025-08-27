import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { Post } from '../../../models/post.model';
import { PostService } from '../../../services/post/post.service';
import { ReviewService } from '../../../services/review/review.service';

@Component({
  selector: 'app-review-detail',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './review-detail.component.html',
  styleUrls: ['./review-detail.component.css']
})
export class ReviewDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private postSrv = inject(PostService);
  private reviewSrv = inject(ReviewService);

  post?: Post;
  loading = true;
  note = '';
  error = '';

  async ngOnInit(): Promise<void> {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    try {
      this.post = await this.postSrv.getById(id);
      this.loading = false;
    } catch {
      this.error = 'Artikel niet gevonden.';
      this.loading = false;
    }
  }

  async approve() {
    if (!this.post) return;
    await this.reviewSrv.approve(this.post.id!);
    this.router.navigate(['/admin/review']);
  }

  async reject() {
    if (!this.post) return;
    await this.reviewSrv.reject(this.post.id!, this.note || '');
    this.router.navigate(['/admin/review']);
  }
}