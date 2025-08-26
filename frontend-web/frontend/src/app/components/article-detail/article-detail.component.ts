import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule, DatePipe } from '@angular/common';
import { firstValueFrom } from 'rxjs';

import { PostService } from '../../services/post.service';
import { BookmarkService } from '../../services/bookmark.service';
import { AuthService } from '../../services/auth.service';

import { Post } from '../../models/post.model';
import { CommentsListComponent } from '../comments-list/comments-list.component';

@Component({
  selector: 'app-article-detail',
  standalone: true,
  imports: [CommonModule, DatePipe, CommentsListComponent],
  templateUrl: './article-detail.component.html',
  styleUrls: ['./article-detail.component.css']
})
export class ArticleDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private postService = inject(PostService);
  private bookmarkService = inject(BookmarkService);
  private auth = inject(AuthService);

  post?: Post;
  loading = true;
  error = '';

  bookmarked = false;
  bookmarking = false;

  async ngOnInit() {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    try {
      this.post = await this.postService.getById(id);

      // Indien ingelogd: check of dit artikel gebookmarkt is
      const username = await firstValueFrom(this.auth.username$);
      if (username && this.post?.id != null) {
        this.bookmarked = await this.bookmarkService.isBookmarked(this.post.id);
      }
    } catch {
      this.error = 'Artikel kon niet geladen worden.';
    } finally {
      this.loading = false;
    }
  }

  async toggleBookmark() {
    if (!this.post || this.post.id == null || this.bookmarking) return;

    const username = await firstValueFrom(this.auth.username$);
    if (!username) {
      await this.router.navigate(['/login']);
      return;
    }

    this.bookmarking = true;
    const next = !this.bookmarked;
    this.bookmarked = next; // optimistisch

    try {
      if (next) {
        await this.bookmarkService.add(this.post.id);
      } else {
        await this.bookmarkService.remove(this.post.id);
      }
    } catch {
      // rollback bij fout
      this.bookmarked = !next;
    } finally {
      this.bookmarking = false;
    }
  }
}
