import { Component, OnInit, inject } from '@angular/core';
import { Router } from '@angular/router';
import { NgFor, NgIf } from '@angular/common';
import { Post } from '../../../models/post.model';
import { PostService } from '../../../services/post/post.service';
import { DraftCardComponent } from '../draft-card/draft-card.component';

@Component({
  selector: 'app-draft-list',
  standalone: true,
  imports: [NgIf, NgFor, DraftCardComponent],
  templateUrl: './draft-list.component.html',
  styleUrls: ['./draft-list.component.css'],
})
export class DraftListComponent implements OnInit {
  private postService = inject(PostService);
  private router = inject(Router);

  loading = true;
  error = '';
  drafts: Post[] = [];

  async ngOnInit() {
    await this.load();
  }

  async load() {
    this.loading = true;
    this.error = '';
    try {
      this.drafts = await this.postService.getDrafts();
    } catch (e) {
      this.error = 'Kon drafts niet laden.';
    } finally {
      this.loading = false;
    }
  }

  async handlePublish(post: Post) {
    try {
      await this.postService.submitDraft(post.id);
      this.drafts = this.drafts.filter(d => d.id !== post.id);
    } catch {
      alert('Indienen mislukte.');
    }
  }

  handleEdit(post: Post) {
    this.router.navigate(['/admin/update', post.id], { state: { post } });
  }

  newArticle() {
    this.router.navigate(['/admin/write']);
  }
}
