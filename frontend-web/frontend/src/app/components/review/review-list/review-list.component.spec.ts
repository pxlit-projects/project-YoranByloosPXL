import { TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { provideRouter } from '@angular/router';

import { ReviewListComponent } from './review-list.component';
import { ReviewService } from '../../../services/review/review.service';
import { Post } from '../../../models/post.model';

describe('ReviewListComponent', () => {
  let reviewSrv: jasmine.SpyObj<ReviewService>;

  const POSTS: Post[] = [
    {
      id: 1, title: 'A', content: '...', author: 'ann',
      status: 'INGEDIEND', published: false,
      createdAt: '2024-01-01T00:00:00', updatedAt: '2024-01-01T00:00:00'
    },
    {
      id: 2, title: 'B', content: '...', author: 'bob',
      status: 'INGEDIEND', published: false,
      createdAt: '2024-01-02T00:00:00', updatedAt: '2024-01-02T00:00:00'
    },
  ];

  beforeEach(async () => {
    reviewSrv = jasmine.createSpyObj<ReviewService>('ReviewService', ['getReviewablePosts']);

    await TestBed.configureTestingModule({
      imports: [ReviewListComponent],
      providers: [
        { provide: ReviewService, useValue: reviewSrv },
        provideRouter([]), 
      ],
    }).compileComponents();
  });

  it('laadt reviewbare posts (success pad)', () => {
    reviewSrv.getReviewablePosts.and.returnValue(of(POSTS));

    const fixture = TestBed.createComponent(ReviewListComponent);
    fixture.detectChanges(); 

    const comp = fixture.componentInstance;
    expect(reviewSrv.getReviewablePosts).toHaveBeenCalled();

    expect(comp.loading).toBeFalse();
    expect(comp.error).toBe('');
    expect(comp.posts.length).toBe(2);

    const cards = fixture.nativeElement.querySelectorAll('app-review-card');
    expect(cards.length).toBe(2);
  });

  it('zet foutmelding wanneer service faalt', () => {
    reviewSrv.getReviewablePosts.and.returnValue(throwError(() => new Error('boom')));

    const fixture = TestBed.createComponent(ReviewListComponent);
    fixture.detectChanges();

    const comp = fixture.componentInstance;
    expect(comp.loading).toBeFalse();
    expect(comp.error).toBe('Kon reviewbare artikels niet laden.');
    expect(comp.posts.length).toBe(0);

    const err = fixture.nativeElement.querySelector('.error');
    expect(err?.textContent).toContain('Kon reviewbare artikels niet laden.');
  });
});
