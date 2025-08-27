import { TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { provideRouter } from '@angular/router';

import { ReviewCardComponent } from './review-card.component';
import { Post } from '../../../models/post.model';
import { Router } from '@angular/router';

describe('ReviewCardComponent', () => {
  let router: jasmine.SpyObj<Router>;

  const POST: Post = {
    id: 42,
    title: 'Titel',
    content: 'Inhoud…',
    author: 'ann',
    status: 'INGEDIEND',
    published: false,
    createdAt: '2024-01-10T10:00:00',
    updatedAt: '2024-01-10T10:00:00'
  };

  beforeEach(async () => {
    router = jasmine.createSpyObj<Router>('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [ReviewCardComponent],
      providers: [
        { provide: Router, useValue: router },
        provideRouter([]),
      ],
    }).compileComponents();
  });

  it('navigeert naar /admin/review/:id bij klik op Reviewen', () => {
    const fixture = TestBed.createComponent(ReviewCardComponent);
    const comp = fixture.componentInstance;
    comp.post = POST;
    fixture.detectChanges();

    const btn = fixture.debugElement.query(By.css('.btn')).nativeElement as HTMLButtonElement;
    btn.click();

    expect(router.navigate).toHaveBeenCalledOnceWith(['/admin/review', 42]);
  });

  it('rendert titel en meta', () => {
    const fixture = TestBed.createComponent(ReviewCardComponent);
    const comp = fixture.componentInstance;
    comp.post = POST;
    fixture.detectChanges();

    expect(fixture.debugElement.query(By.css('.title')).nativeElement.textContent).toContain('Titel');
    expect(fixture.debugElement.query(By.css('.meta')).nativeElement.textContent).toContain('ann');
  });
});
