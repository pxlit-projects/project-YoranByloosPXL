import { TestBed } from '@angular/core/testing';
import { ArticleDetailComponent } from './article-detail.component';
import { ActivatedRoute, Router, provideRouter, convertToParamMap } from '@angular/router';
import { of } from 'rxjs';

import { PostService } from '../../../services/post/post.service';
import { BookmarkService } from '../../../services/bookmark/bookmark.service';
import { AuthService } from '../../../services/auth/auth.service';
import { Component, Input } from '@angular/core';
import { HttpClientTestingModule } from '@angular/common/http/testing';

@Component({
  selector: 'app-comments-list',
  standalone: true,
  template: ''
})
class CommentsListStub { @Input() postId?: number; }

describe('ArticleDetailComponent', () => {
  async function setup(routeId = '5', authUsername: string | null = 'ann') {
    const routeMock = {
      snapshot: { paramMap: convertToParamMap({ id: routeId }) }
    };

    const postSvc = jasmine.createSpyObj<PostService>('PostService', ['getById']);
    postSvc.getById.and.returnValue(Promise.resolve({
      id: 5,
      title: 'Titel',
      content: 'Inhoud',
      author: 'Ann',
      status: 'GEPUBLICEERD',
      published: true,
      createdAt: '2025-01-01T12:30:00',
      updatedAt: '2025-01-01T12:30:00',
    }));

    const bmSvc = jasmine.createSpyObj<BookmarkService>('BookmarkService', ['isBookmarked', 'add', 'remove']);
    bmSvc.isBookmarked.and.returnValue(Promise.resolve(true));
    bmSvc.add.and.returnValue(Promise.resolve());
    bmSvc.remove.and.returnValue(Promise.resolve());

    const authSvc = { username$: of(authUsername) } as Partial<AuthService> as AuthService;

    await TestBed.configureTestingModule({
      imports: [ArticleDetailComponent, CommentsListStub, HttpClientTestingModule],
      providers: [
        provideRouter([]),
        { provide: ActivatedRoute, useValue: routeMock },
        { provide: PostService, useValue: postSvc },
        { provide: BookmarkService, useValue: bmSvc },
        { provide: AuthService, useValue: authSvc },
      ],
    }).compileComponents();

    const fixture = TestBed.createComponent(ArticleDetailComponent);
    const comp = fixture.componentInstance;
    const router = TestBed.inject(Router);

    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();

    return { fixture, comp, router, postSvc, bmSvc };
  }

  it('laadt post en zet bookmarked state', async () => {
    const { comp, bmSvc } = await setup();
    expect(comp.post?.id).toBe(5);
    expect(bmSvc.isBookmarked).toHaveBeenCalledOnceWith(5);
    expect(comp.bookmarked).toBeTrue();
    expect(comp.loading).toBeFalse();
    expect(comp.error).toBe('');
  });

  it('navigatie naar /login wanneer geen user bij toggle', async () => {
    const { comp, router } = await setup('5', null);
    const navSpy = spyOn(router, 'navigate').and.resolveTo(true);

    await comp.toggleBookmark();

    expect(navSpy).toHaveBeenCalledOnceWith(['/login']);
  });

  it('toggleBookmark: remove en add paths', async () => {
    const { comp, bmSvc } = await setup();

    await comp.toggleBookmark(); 
    expect(bmSvc.remove).toHaveBeenCalledOnceWith(5);
    expect(comp.bookmarked).toBeFalse();

    await comp.toggleBookmark(); 
    expect(bmSvc.add).toHaveBeenCalledOnceWith(5);
    expect(comp.bookmarked).toBeTrue();
  });
});
