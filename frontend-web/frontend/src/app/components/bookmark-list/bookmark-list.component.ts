import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BookmarkCardComponent } from '../bookmark-card/bookmark-card.component';
import { BookmarkService } from '../../services/bookmark.service';
import { Post } from '../../models/post.model';

@Component({
  selector: 'app-bookmark-list',
  standalone: true,
  imports: [CommonModule, BookmarkCardComponent],
  templateUrl: './bookmark-list.component.html',
  styleUrls: ['./bookmark-list.component.css']
})
export class BookmarkListComponent implements OnInit {
  loading = true;
  error = '';
  items: Post[] = [];

  constructor(private bookmarks: BookmarkService) {}

  async ngOnInit() {
    try {
      this.items = await this.bookmarks.getMy();
    } catch {
      this.error = 'Kon je bookmarks niet laden.';
    } finally {
      this.loading = false;
    }
  }

  onRemoved(id: number) {
    this.items = this.items.filter(p => p.id !== id);
  }
}
