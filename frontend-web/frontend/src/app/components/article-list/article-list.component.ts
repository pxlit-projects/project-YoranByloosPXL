import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Post } from '../../models/post.model';
import { PostService } from '../../services/post.service';
import { ArticleFilter, ArticleFilterComponent } from '../../components/article-filter/article-filter.component';
import { ArticleCardComponent } from '../../components/article-card/article-card.component';

@Component({
  selector: 'app-article-list',
  standalone: true,
  imports: [CommonModule, ArticleFilterComponent, ArticleCardComponent],
  templateUrl: './article-list.component.html',
  styleUrls: ['./article-list.component.css']
})
export class ArticleListComponent {
  constructor(private postsApi: PostService) {
    this.loadPublished();
  }

  posts = signal<Post[]>([]);
  loading = signal<boolean>(false);
  error = signal<string>('');

  /** Standaard: gepubliceerde posts */
  private loadPublished() {
    this.loading.set(true);
    this.error.set('');
    this.postsApi.getPublished().subscribe({
      next: res => { this.posts.set(res); this.loading.set(false); },
      error: () => { this.error.set('Kon artikels niet laden.'); this.loading.set(false); }
    });
  }

  /** Filter via backend endpoint */
  onSearch(f: ArticleFilter) {
    // Als alles leeg is: toon gewoon de gepubliceerde lijst.
    if (!f.keyword && !f.author && !f.date) {
      this.loadPublished();
      return;
    }

    this.loading.set(true);
    this.error.set('');
    this.postsApi.filter({
      keyword: f.keyword,
      author: f.author,
      date: f.date
    }).subscribe({
      next: res => { this.posts.set(res); this.loading.set(false); },
      error: () => { this.error.set('Filteren mislukt.'); this.loading.set(false); }
    });
  }
}
