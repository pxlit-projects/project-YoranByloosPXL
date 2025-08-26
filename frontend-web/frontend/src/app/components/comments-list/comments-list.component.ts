// src/app/components/comments-list/comments-list.component.ts
import { Component, Input, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CommentService } from '../../services/comment.service';
import { Comment } from '../../models/comment.model';
import { AuthService } from '../../services/auth.service';
import { CommentsCardComponent } from '../comments-card/comments-card.component';
import { firstValueFrom } from 'rxjs';

@Component({
  selector: 'app-comments-list',
  standalone: true,
  imports: [CommonModule, FormsModule, CommentsCardComponent],
  templateUrl: './comments-list.component.html',
  styleUrls: ['./comments-list.component.css']
})
export class CommentsListComponent {
  private commentService = inject(CommentService);
  auth = inject(AuthService);

  @Input() postId!: number;

  comments: Comment[] = [];
  newContent = '';
  adding = false;

  async ngOnInit() {
    await this.refresh();
  }

  private async refresh() {
    this.comments = await firstValueFrom(
      this.commentService.getByPostId(this.postId)
    );
  }

  async add() {
    if (!this.newContent.trim()) return;
    this.adding = true;
    try {
      const created = await this.commentService.add(this.postId, this.newContent.trim());
      this.comments = [created, ...this.comments];
      this.newContent = '';
    } finally {
      this.adding = false;
    }
  }

  onUpdated(updated: Comment) {
    this.comments = this.comments.map(c => (c.id === updated.id ? updated : c));
  }

  onDeleted(id: number) {
    this.comments = this.comments.filter(c => c.id !== id);
  }
}
