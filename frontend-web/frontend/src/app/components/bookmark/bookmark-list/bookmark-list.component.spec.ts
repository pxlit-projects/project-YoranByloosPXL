import { TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { provideRouter } from '@angular/router';

import { BookmarkListComponent } from './bookmark-list.component';
import { BookmarkCardComponent } from '../bookmark-card/bookmark-card.component';
import { BookmarkService } from '../../../services/bookmark/bookmark.service';
import { Post } from '../../../models/post.model';

describe('BookmarkListComponent', () => {
  let svc: jasmine.SpyObj<BookmarkService>;

  const sample: Post[] = [
    {
      id: 1,
      title: 'Titel A',
      content: 'Inhoud A',
      author: 'ann',
      status: 'GEPUBLICEERD',
      published: true,
      createdAt: '2024-01-01T00:00:00',
      updatedAt: '2024-01-02T00:00:00',
    },
    {
      id: 2,
      title: 'Titel B',
      content: 'Inhoud B',
      author: 'bob',
      status: 'GEPUBLICEERD',
      published: true,
      createdAt: '2024-02-01T00:00:00',
      updatedAt: '2024-02-02T00:00:00',
    },
  ];

  beforeEach(async () => {
    svc = jasmine.createSpyObj<BookmarkService>('BookmarkService', ['getMy', 'remove']);

    await TestBed.configureTestingModule({
      imports: [
        BookmarkListComponent,
        BookmarkCardComponent,
      ],
      providers: [
        { provide: BookmarkService, useValue: svc },
        provideRouter([]),
      ],
    }).compileComponents();
  });

  it('laadt bookmarks in ngOnInit en toont items', async () => {
    svc.getMy.and.returnValue(Promise.resolve(sample));

    const fixture = TestBed.createComponent(BookmarkListComponent);
    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();

    const comp = fixture.componentInstance;
    expect(svc.getMy).toHaveBeenCalled();
    expect(comp.loading).toBeFalse();
    expect(comp.error).toBe('');
    expect(comp.items.length).toBe(2);

    const cards = fixture.debugElement.queryAll(By.directive(BookmarkCardComponent));
    expect(cards.length).toBe(2);
  });

  it('toont foutmelding als ophalen faalt', async () => {
    svc.getMy.and.returnValue(Promise.reject(new Error('boom')));

    const fixture = TestBed.createComponent(BookmarkListComponent);
    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();

    const comp = fixture.componentInstance;
    expect(comp.loading).toBeFalse();
    expect(comp.items.length).toBe(0);
    expect(comp.error).toBe('Kon je bookmarks niet laden.');

    const err = fixture.debugElement.query(By.css('.state.state--err'));
    expect(err).toBeTruthy();
    const cards = fixture.debugElement.queryAll(By.directive(BookmarkCardComponent));
    expect(cards.length).toBe(0);
  });

  it('verwijdert item uit lijst wanneer child (removed) emit', async () => {
    svc.getMy.and.returnValue(Promise.resolve(sample));

    const fixture = TestBed.createComponent(BookmarkListComponent);
    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();

    const cardDes = fixture.debugElement.queryAll(By.directive(BookmarkCardComponent));
    const firstCard = cardDes[0].componentInstance as BookmarkCardComponent;
    firstCard.removed.emit(1);

    fixture.detectChanges();

    const comp = fixture.componentInstance;
    expect(comp.items.length).toBe(1);
    expect(comp.items[0].id).toBe(2);

    const cardsAfter = fixture.debugElement.queryAll(By.directive(BookmarkCardComponent));
    expect(cardsAfter.length).toBe(1);
  });
});
