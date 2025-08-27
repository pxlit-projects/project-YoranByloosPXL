import { Component, Input } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { Post } from '../../../models/post.model';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-article-card',
  standalone: true,
  imports: [CommonModule, DatePipe, RouterLink],
  templateUrl: './article-card.component.html',
  styleUrls: ['./article-card.component.css']
})
export class ArticleCardComponent {
  @Input() post!: Post;

  get excerpt(): string {
    const txt = this.post?.content ?? '';
    return txt.length > 220 ? txt.slice(0, 220) + '…' : txt;
  }
}
