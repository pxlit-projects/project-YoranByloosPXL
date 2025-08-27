import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { CommentService } from './comment.service';
import { AuthService } from '../auth/auth.service';
import { Comment } from '../../models/comment.model';

describe('CommentService', () => {
  let service: CommentService;
  let http: HttpTestingController;

  const BASE = 'http://localhost:8084/comments';
  const mockAuth = (username: string | null) => ({ username$: of(username) });

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [{ provide: AuthService, useValue: mockAuth('ann') }]
    });
    service = TestBed.inject(CommentService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('getByPostId(): GET /comments/:postId', (done) => {
    const data: Comment[] = [{ id: 1, postId: 7, username: 'ann', content: 'hoi', createdAt: '2024', updatedAt: "2024" }];

    service.getByPostId(7).subscribe(res => {
      expect(res).toEqual(data);
      done();
    });

    const req = http.expectOne(`${BASE}/7`);
    expect(req.request.method).toBe('GET');
    req.flush(data);
  });

  it('gooit fout als niet ingelogd', async () => {
    TestBed.resetTestingModule();
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [{ provide: AuthService, useValue: mockAuth(null) }]
    });
    const s = TestBed.inject(CommentService);

    await expectAsync(s.add(1, 'x')).toBeRejectedWithError('Je bent niet ingelogd');
    await expectAsync(s.update(1, 'x')).toBeRejectedWithError('Je bent niet ingelogd');
    await expectAsync(s.delete(1)).toBeRejectedWithError('Je bent niet ingelogd');
  });
});
