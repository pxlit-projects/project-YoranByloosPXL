import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Post } from '../../models/post.model';
import { BookmarkService } from '../../services/bookmark.service';

@Component({
  selector: 'app-bookmark-card',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './bookmark-card.component.html',
  styleUrls: ['./bookmark-card.component.css']
})
export class BookmarkCardComponent {
  @Input({ required: true }) post!: Post;
  @Output() removed = new EventEmitter<number>();

  busy = false;

  constructor(private bookmarks: BookmarkService) {}

  async removeBookmark() {
    if (this.busy) return;
    this.busy = true;
    try {
      await this.bookmarks.remove(this.post.id);
      this.removed.emit(this.post.id);
    } finally {
      this.busy = false;
    }
  }
}
