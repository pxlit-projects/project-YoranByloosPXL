import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of, Subject, throwError } from 'rxjs';

import { ArticleListComponent } from './article-list.component';
import { PostService } from '../../../services/post/post.service';
import { Post } from '../../../models/post.model';

describe('ArticleListComponent', () => {
  let postsApi: jasmine.SpyObj<PostService>;

  const samplePosts: Post[] = [
    {
      id: 1, title: 'A', content: 'x', author: 'ann',
      status: 'GEPUBLICEERD', published: true,
      createdAt: new Date().toISOString(), updatedAt: new Date().toISOString()
    },
    {
      id: 2, title: 'B', content: 'y', author: 'bob',
      status: 'GEPUBLICEERD', published: true,
      createdAt: new Date().toISOString(), updatedAt: new Date().toISOString()
    },
  ];

  beforeEach(async () => {
    postsApi = jasmine.createSpyObj<PostService>('PostService', ['getPublished', 'filter']);

    await TestBed.configureTestingModule({
      imports: [ArticleListComponent], 
      providers: [
        { provide: PostService, useValue: postsApi },
        provideRouter([]), 
      ],
    }).compileComponents();
  });

  it('laat laad-state zien en rendert posts na succes', () => {
    const subj = new Subject<Post[]>();
    postsApi.getPublished.and.returnValue(subj.asObservable());

    const fixture = TestBed.createComponent(ArticleListComponent);
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('.state')?.textContent).toContain('Laden');

    subj.next(samplePosts);
    subj.complete();
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('.state')).toBeNull();
    const cards = fixture.nativeElement.querySelectorAll('app-article-card');
    expect(cards.length).toBe(samplePosts.length);
  });

  it('toont foutmelding wanneer laden faalt', () => {
    postsApi.getPublished.and.returnValue(throwError(() => new Error('boom')));
    const fixture = TestBed.createComponent(ArticleListComponent);
    fixture.detectChanges();
    const err = fixture.nativeElement.querySelector('.state.error');
    expect(err?.textContent).toContain('Kon artikels niet laden.');
  });

  it('onSearch({}) herlaadt published', () => {
    postsApi.getPublished.and.returnValues(of([]), of([]));
    const fixture = TestBed.createComponent(ArticleListComponent);
    fixture.detectChanges();
    const comp = fixture.componentInstance;
    comp.onSearch({});
    expect(postsApi.getPublished).toHaveBeenCalledTimes(2);
  });

  it('onSearch met criteria roept filter en toont resultaten', () => {
    postsApi.getPublished.and.returnValue(of([]));
    postsApi.filter.and.returnValue(of(samplePosts));
    const fixture = TestBed.createComponent(ArticleListComponent);
    fixture.detectChanges();

    const comp = fixture.componentInstance;
    comp.onSearch({ keyword: 'A', author: 'ann', date: '2024-01-01T00:00:00' });
    fixture.detectChanges();

    expect(postsApi.filter).toHaveBeenCalledWith({
      keyword: 'A', author: 'ann', date: '2024-01-01T00:00:00'
    });

    const cards = fixture.nativeElement.querySelectorAll('app-article-card');
    expect(cards.length).toBe(samplePosts.length);
  });

  it('onSearch error zet foutmelding', () => {
    postsApi.getPublished.and.returnValue(of([]));
    postsApi.filter.and.returnValue(throwError(() => new Error('nope')));
    const fixture = TestBed.createComponent(ArticleListComponent);
    fixture.detectChanges();

    const comp = fixture.componentInstance;
    comp.onSearch({ keyword: 'x' });
    fixture.detectChanges();

    const err = fixture.nativeElement.querySelector('.state.error');
    expect(err?.textContent).toContain('Filteren mislukt.');
  });
});
