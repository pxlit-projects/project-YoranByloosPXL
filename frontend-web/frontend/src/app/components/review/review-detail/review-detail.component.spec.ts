import { TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { provideRouter, convertToParamMap } from '@angular/router';

import { ReviewDetailComponent } from './review-detail.component';
import { Post } from '../../../models/post.model';
import { PostService } from '../../../services/post/post.service';
import { ReviewService } from '../../../services/review/review.service';
import { ActivatedRoute, Router } from '@angular/router';

describe('ReviewDetailComponent', () => {
  let postSrv: jasmine.SpyObj<PostService>;
  let reviewSrv: jasmine.SpyObj<ReviewService>;
  let router: jasmine.SpyObj<Router>;

  const POST: Post = {
    id: 7,
    title: 'Te reviewen post',
    content: 'Body',
    author: 'bob',
    status: 'INGEDIEND',
    published: false,
    createdAt: '2024-01-01T00:00:00',
    updatedAt: '2024-01-01T00:00:00',
  };

  beforeEach(async () => {
    postSrv = jasmine.createSpyObj<PostService>('PostService', ['getById']);
    reviewSrv = jasmine.createSpyObj<ReviewService>('ReviewService', ['approve', 'reject']);
    router = jasmine.createSpyObj<Router>('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [ReviewDetailComponent], // standalone (FormsModule + CommonModule al geïmporteerd)
      providers: [
        { provide: PostService, useValue: postSrv },
        { provide: ReviewService, useValue: reviewSrv },
        { provide: Router, useValue: router },
        provideRouter([]),
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { paramMap: convertToParamMap({ id: '7' }) } },
        },
      ],
    }).compileComponents();
  });

  it('laadt post in ngOnInit (success)', async () => {
    postSrv.getById.and.returnValue(Promise.resolve(POST));

    const fixture = TestBed.createComponent(ReviewDetailComponent);
    fixture.detectChanges();               // triggert ngOnInit
    await fixture.whenStable();
    fixture.detectChanges();

    const comp = fixture.componentInstance;
    expect(postSrv.getById).toHaveBeenCalledOnceWith(7);
    expect(comp.loading).toBeFalse();
    expect(comp.error).toBe('');
    expect(comp.post?.id).toBe(7);

    // titel staat in DOM
    const titleEl = fixture.debugElement.query(By.css('.title')).nativeElement as HTMLElement;
    expect(titleEl.textContent).toContain('Te reviewen post');
  });

  it('zet foutmelding wanneer post niet gevonden', async () => {
    postSrv.getById.and.returnValue(Promise.reject(new Error('not found')));

    const fixture = TestBed.createComponent(ReviewDetailComponent);
    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();

    const comp = fixture.componentInstance;
    expect(comp.loading).toBeFalse();
    expect(comp.error).toBe('Artikel niet gevonden.');
    expect(comp.post).toBeUndefined();
  });

  it('approve(): keurt goed en navigeert terug', async () => {
    reviewSrv.approve.and.returnValue(Promise.resolve());

    const fixture = TestBed.createComponent(ReviewDetailComponent);
    const comp = fixture.componentInstance;
    comp.post = { ...POST }; // overslaan van ngOnInit
    await comp.approve();

    expect(reviewSrv.approve).toHaveBeenCalledOnceWith(7);
    expect(router.navigate).toHaveBeenCalledOnceWith(['/admin/review']);
  });

  it('reject(): wijst af met opmerking en navigeert terug', async () => {
    reviewSrv.reject.and.returnValue(Promise.resolve());

    const fixture = TestBed.createComponent(ReviewDetailComponent);
    const comp = fixture.componentInstance;
    comp.post = { ...POST };
    comp.note = 'Te kort.';
    await comp.reject();

    expect(reviewSrv.reject).toHaveBeenCalledOnceWith(7, 'Te kort.');
    expect(router.navigate).toHaveBeenCalledOnceWith(['/admin/review']);
  });
});
