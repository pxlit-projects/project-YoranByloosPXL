import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { ArticleListComponent } from './components/article-list/article-list.component';
import { WriteArticleComponent } from './components/write-article/write-article.component';
import { DraftListComponent } from './components/draft-list/draft-list.component';
import { UpdateArticleComponent } from './components/update-article/update-article.component';
import { ReviewListComponent } from './components/review-list/review-list.component';
import { ReviewDetailComponent } from './components/review-detail/review-detail.component';
import { SubmissionsComponent } from './components/submissions/submissions.component';
import { ArticleDetailComponent } from './components/article-detail/article-detail.component';
import { BookmarkListComponent } from './components/bookmark-list/bookmark-list.component';

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
