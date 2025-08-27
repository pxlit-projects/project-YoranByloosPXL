import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { ArticleListComponent } from './components/article/article-list/article-list.component';
import { DraftListComponent } from './components/draft/draft-list/draft-list.component';
import { UpdateArticleComponent } from './components/article/update-article/update-article.component';
import { WriteArticleComponent } from './components/article/write-article/write-article.component';
import { ReviewListComponent } from './components/review/review-list/review-list.component';
import { ReviewDetailComponent } from './components/review/review-detail/review-detail.component';
import { SubmissionsComponent } from './components/submissions/submissions.component';
import { ArticleDetailComponent } from './components/article/article-detail/article-detail.component';
import { BookmarkListComponent } from './components/bookmark/bookmark-list/bookmark-list.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: '', component: ArticleListComponent },
  { path: 'admin/drafts', component: DraftListComponent },
  { path: 'admin/update/:id', component: UpdateArticleComponent },
  { path: 'admin/write', component: WriteArticleComponent },
  { path: 'admin/review', component: ReviewListComponent },
  { path: 'admin/review/:id', component: ReviewDetailComponent },
  { path: 'admin/submissions', component: SubmissionsComponent },
  { path: 'posts/:id', component: ArticleDetailComponent },
  { path: 'bookmarks', component: BookmarkListComponent },
  { path: '**', redirectTo: '' }
];
