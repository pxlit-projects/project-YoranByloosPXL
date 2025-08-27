// src/app/admin/submissions/submissions-page.component.ts
import { Component, OnInit, inject } from '@angular/core';
import { CommonModule, NgFor, NgIf } from '@angular/common';
import { PostService } from '../../services/post/post.service';
import { Post } from '../../models/post.model';

type Grouped = { accepted: Post[]; rejected: Post[]; submitted: Post[] };

@Component({
  selector: 'app-submissions',
  standalone: true,
  imports: [CommonModule, NgFor, NgIf],
  templateUrl: './submissions.component.html',
  styleUrls: ['./submissions.component.css']
})
export class SubmissionsComponent implements OnInit {
  private postService = inject(PostService);

  loading = true;
  error = '';
  grouped: Grouped = { accepted: [], rejected: [], submitted: [] };

  ngOnInit(): void {
    this.refresh();
  }

  async refresh() {
    this.loading = true;
    this.error = '';
    this.postService.getMySubmissions().subscribe({
      next: posts => {
        this.grouped = {
          accepted: posts.filter(p => p.status === 'GOEDGEKEURD'),
          rejected: posts.filter(p => p.status === 'GEWEIGERD'),
          submitted: posts.filter(p => p.status === 'INGEDIEND')
        };
        this.loading = false;
      },
      error: () => {
        this.error = 'Kon je inzendingen niet laden.';
        this.loading = false;
      }
    });
  }

  async publish(p: Post) {
    try {
      await this.postService.publish(p.id);
      await this.refresh();
    } catch {
      this.error = 'Publiceren mislukt.';
    }
  }

  async backToDraft(p: Post) {
    try {
      await this.postService.toDraft(p.id);
      await this.refresh();
    } catch {
      this.error = 'Terugzetten naar draft mislukt.';
    }
  }
}
