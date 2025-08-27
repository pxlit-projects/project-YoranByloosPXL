import { TestBed } from '@angular/core/testing';
import { CommentsCardComponent } from './comments-card.component';
import { CommentService } from '../../../services/comment/comment.service';
import { Comment } from '../../../models/comment.model';

describe('CommentsCardComponent', () => {
  let api: jasmine.SpyObj<CommentService>;

  const base: Comment = {
    id: 5,
    postId: 3,
    username: 'ann',
    content: 'originele tekst',
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString(),
  };

  beforeEach(async () => {
    api = jasmine.createSpyObj<CommentService>('CommentService', ['update', 'delete']);

    await TestBed.configureTestingModule({
      imports: [CommentsCardComponent],
      providers: [{ provide: CommentService, useValue: api }],
    }).compileComponents();
  });

  function setup(owner = 'ann') {
    const fixture = TestBed.createComponent(CommentsCardComponent);
    const comp = fixture.componentInstance;
    comp.comment = { ...base };
    comp.currentUser = owner;
    comp.ngOnChanges();
    fixture.detectChanges();
    return { fixture, comp };
  }

  it('ngOnChanges kopieert content naar value', () => {
    const { comp } = setup();
    expect(comp.value).toBe('originele tekst');
  });

  it('isOwner is true als currentUser == username', () => {
    const { comp } = setup('ann');
    expect(comp.isOwner).toBeTrue();
  });

  it('isOwner is false voor andere user', () => {
    const { comp } = setup('bob');
    expect(comp.isOwner).toBeFalse();
  });

  it('edit-flow: eerste klik opent edit, tweede klik slaat op en emit updated', async () => {
    const { comp, fixture } = setup('ann');
    const emitSpy = spyOn(comp.updated, 'emit');
    const saved: Comment = { ...base, content: 'nieuw' };
    api.update.and.resolveTo(saved);

    await comp.onEditClick();
    expect(comp.editing).toBeTrue();

    comp.value = 'nieuw';
    await comp.onEditClick();
    fixture.detectChanges();

    expect(api.update).toHaveBeenCalledOnceWith(base.id!, 'nieuw');
    expect(emitSpy).toHaveBeenCalledOnceWith(saved);
    expect(comp.editing).toBeFalse();
  });

  it('cancelEdit zet editing=false en herstelt value', () => {
    const { comp } = setup('ann');
    comp.editing = true;
    comp.value = 'gewijzigd';

    comp.cancelEdit();

    expect(comp.editing).toBeFalse();
    expect(comp.value).toBe(base.content);
  });

  it('delete: alleen owner → service.delete en emit', async () => {
    const { comp } = setup('ann');
    const delSpy = spyOn(comp.deleted, 'emit');
    api.delete.and.resolveTo();

    await comp.onDeleteClick();

    expect(api.delete).toHaveBeenCalledOnceWith(base.id!);
    expect(delSpy).toHaveBeenCalledOnceWith(base.id!);
  });

  it('niet-owner: edit/delete doen niets', async () => {
    const { comp } = setup('bob');
    const updSpy = spyOn(comp.updated, 'emit');
    const delSpy = spyOn(comp.deleted, 'emit');

    await comp.onEditClick();
    await comp.onDeleteClick();

    expect(comp.editing).toBeFalse();
    expect(api.update).not.toHaveBeenCalled();
    expect(api.delete).not.toHaveBeenCalled();
    expect(updSpy).not.toHaveBeenCalled();
    expect(delSpy).not.toHaveBeenCalled();
  });
});
