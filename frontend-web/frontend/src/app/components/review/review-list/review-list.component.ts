import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReviewService } from '../../../services/review/review.service';
import { Post } from '../../../models/post.model';
import { ReviewCardComponent } from '../review-card/review-card.component';

@Component({
  selector: 'app-review-list',
  standalone: true,
  imports: [CommonModule, ReviewCardComponent],
  templateUrl: './review-list.component.html',
  styleUrls: ['./review-list.component.css']
})
export class ReviewListComponent implements OnInit {
  private reviewSrv = inject(ReviewService);

  loading = true;
  error = '';
  posts: Post[] = [];

  ngOnInit(): void {
    this.reviewSrv.getReviewablePosts().subscribe({
      next: (data) => { this.posts = data; this.loading = false; },
      error: () => { this.error = 'Kon reviewbare artikels niet laden.'; this.loading = false; }
    });
  }
}
