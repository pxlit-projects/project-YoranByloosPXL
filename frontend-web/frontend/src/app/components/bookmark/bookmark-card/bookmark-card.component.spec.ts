import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { DatePipe } from '@angular/common';

import { BookmarkCardComponent } from './bookmark-card.component';
import { BookmarkService } from '../../../services/bookmark/bookmark.service';
import { Post } from '../../../models/post.model';

describe('BookmarkCardComponent', () => {
  let bookmarks: jasmine.SpyObj<BookmarkService>;
  const sample: Post = {
    id: 5,
    title: 'Post',
    content: 'Body',
    author: 'ann',
    status: 'GEPUBLICEERD',
    published: true,
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString(),
  };

  beforeEach(async () => {
    bookmarks = jasmine.createSpyObj<BookmarkService>('BookmarkService', ['remove']);

    await TestBed.configureTestingModule({
      imports: [BookmarkCardComponent, DatePipe], // DatePipe voor |date
      providers: [
        { provide: BookmarkService, useValue: bookmarks },
        provideRouter([]),
      ],
    }).compileComponents();
  });

  it('klikt op verwijderen → call service + emit id', async () => {
    bookmarks.remove.and.returnValue(Promise.resolve());

    const fixture = TestBed.createComponent(BookmarkCardComponent);
    const comp = fixture.componentInstance;
    comp.post = sample;

    let emitted: number | undefined;
    comp.removed.subscribe(id => (emitted = id));

    fixture.detectChanges();

    const btn: HTMLButtonElement = fixture.nativeElement.querySelector('.icon-btn');
    btn.click();

    await fixture.whenStable(); // wacht op Promise
    fixture.detectChanges();

    expect(bookmarks.remove).toHaveBeenCalledOnceWith(5);
    expect(emitted).toBe(5);
    expect(comp.busy).toBeFalse();
  });

  it('doet niets wanneer busy=true (en knop is disabled)', async () => {
    bookmarks.remove.and.returnValue(Promise.resolve());

    const fixture = TestBed.createComponent(BookmarkCardComponent);
    const comp = fixture.componentInstance;
    comp.post = sample;
    comp.busy = true;
    fixture.detectChanges();

    const btn: HTMLButtonElement = fixture.nativeElement.querySelector('.icon-btn');
    expect(btn.disabled).toBeTrue();

    await comp.removeBookmark(); // directe call
    expect(bookmarks.remove).not.toHaveBeenCalled();
  });
});
