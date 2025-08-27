// src/app/components/comment-card/comment-card.component.ts
import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output, inject, OnChanges } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Comment } from '../../../models/comment.model';
import { CommentService } from '../../../services/comment/comment.service';

@Component({
  selector: 'app-comments-card',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './comments-card.component.html',
  styleUrls: ['./comments-card.component.css']
})
export class CommentsCardComponent implements OnChanges {
  private commentService = inject(CommentService);

  @Input() comment!: Comment;
  @Input() currentUser: string | null = null;

  @Output() updated = new EventEmitter<Comment>();
  @Output() deleted = new EventEmitter<number>();

  editing = false;
  value = '';

  ngOnChanges(): void {
    if (this.comment) this.value = this.comment.content;
  }

  get isOwner(): boolean {
    return !!this.currentUser && this.currentUser === this.comment.username;
  }

  async onEditClick() {
    if (!this.isOwner) return;

    // 1ste klik => naar edit-modus
    if (!this.editing) {
      this.editing = true;
      return;
    }

    // 2de klik => opslaan
    const saved = await this.commentService.update(this.comment.id!, this.value);
    this.editing = false;
    this.updated.emit(saved);
  }

  cancelEdit() {
    this.editing = false;
    this.value = this.comment.content;
  }

  async onDeleteClick() {
    if (!this.isOwner) return;
    await this.commentService.delete(this.comment.id!);
    this.deleted.emit(this.comment.id!);
  }
}
