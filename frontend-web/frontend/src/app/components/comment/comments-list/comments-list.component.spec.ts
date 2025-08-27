import { TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { of } from 'rxjs';

import { CommentsListComponent } from './comments-list.component';
import { CommentsCardComponent } from '../comments-card/comments-card.component';
import { CommentService } from '../../../services/comment/comment.service';
import { AuthService } from '../../../services/auth/auth.service';
import { Comment } from '../../../models/comment.model';

describe('CommentsListComponent', () => {
  let svc: jasmine.SpyObj<CommentService>;

  const sample: Comment[] = [
    { id: 1, postId: 5, username: 'ann', content: 'eerste', createdAt: '2024-01-01T10:00:00', updatedAt: '2024-01-01T12:00:00' },
    { id: 2, postId: 5, username: 'bob', content: 'tweede', createdAt: '2024-01-01T11:00:00', updatedAt: '2024-01-01T12:00:00' },
  ];

  beforeEach(async () => {
    svc = jasmine.createSpyObj<CommentService>('CommentService', [
      'getByPostId', 'add', 'update', 'delete'
    ]);

    await TestBed.configureTestingModule({
      imports: [
        CommentsListComponent,
        CommentsCardComponent,
      ],
      providers: [
        { provide: CommentService, useValue: svc },
        { provide: AuthService, useValue: { username$: of('ann') } },
      ],
    }).compileComponents();
  });

  function create(postId = 5) {
    const fixture = TestBed.createComponent(CommentsListComponent);
    const comp = fixture.componentInstance;
    comp.postId = postId;
    return { fixture, comp };
  }

  it('laadt comments in ngOnInit en rendert cards', async () => {
    svc.getByPostId.and.returnValue(of(sample));

    const { fixture, comp } = create(5);
    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();

    expect(svc.getByPostId).toHaveBeenCalledOnceWith(5);
    expect(comp.comments.length).toBe(2);

    const cards = fixture.debugElement.queryAll(By.directive(CommentsCardComponent));
    expect(cards.length).toBe(2);
  });

  it('add(): voegt nieuwe comment toe en reset input', async () => {
    svc.getByPostId.and.returnValue(of(sample));
    const created: Comment = {
      id: 3, postId: 5, username: 'ann', content: '  nieuw  '.trim(), createdAt: '2024-01-01T12:00:00', updatedAt: '2024-01-01T12:00:00'
    };
    svc.add.and.returnValue(Promise.resolve(created));

    const { fixture, comp } = create(5);
    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();

    comp.newContent = '  nieuw  ';
    await comp.add();
    fixture.detectChanges();

    expect(svc.add).toHaveBeenCalledOnceWith(5, 'nieuw');
    expect(comp.comments[0]).toEqual(created);
    expect(comp.newContent).toBe('');
    expect(comp.adding).toBeFalse();
  });

  it('add(): doet niets bij lege/whitespace input', async () => {
    svc.getByPostId.and.returnValue(of(sample));

    const { fixture, comp } = create(5);
    fixture.detectChanges();
    await fixture.whenStable();

    comp.newContent = '   ';
    await comp.add();

    expect(svc.add).not.toHaveBeenCalled();
    expect(comp.comments.length).toBe(2);
  });

  it('onUpdated(): vervangt aangepast item', () => {
    const { comp } = create(5);
    comp.comments = structuredClone(sample);
    const updated: Comment = { ...sample[0], content: 'gewijzigd' };

    comp.onUpdated(updated);

    expect(comp.comments[0].content).toBe('gewijzigd');
  });

  it('onDeleted(): verwijdert item', () => {
    const { comp } = create(5);
    comp.comments = structuredClone(sample);

    comp.onDeleted(1);

    expect(comp.comments.length).toBe(1);
    expect(comp.comments[0].id).toBe(2);
  });
});
