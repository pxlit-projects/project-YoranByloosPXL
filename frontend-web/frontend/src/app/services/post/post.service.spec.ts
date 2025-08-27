import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { PostService } from './post.service';
import { AuthService } from '../auth/auth.service';
import { Post } from '../../models/post.model';

describe('PostService', () => {
  let service: PostService;
  let http: HttpTestingController;

  const BASE = 'http://localhost:8084/posts';
  const mockAuth = (username: string | null) => ({ username$: of(username) });

  const P: Post = {
    id: 1, title: 'T', content: 'C', author: 'ann',
    status: 'GEPUBLICEERD', published: true, createdAt: '2024', updatedAt: '2024'
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [{ provide: AuthService, useValue: mockAuth('ann') }]
    });
    service = TestBed.inject(PostService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('getPublished(): GET /posts/published', (done) => {
    service.getPublished().subscribe(res => {
      expect(res).toEqual([P]);
      done();
    });

    const req = http.expectOne(`${BASE}/published`);
    expect(req.request.method).toBe('GET');
    req.flush([P]);
  });

  it('filter(): GET /posts/filter met query params', (done) => {
    service.filter({ keyword: 'abc', author: 'ann', date: '2024-01-01T00:00:00' }).subscribe(res => {
      expect(res.length).toBe(1);
      done();
    });

    const req = http.expectOne(r =>
      r.url === `${BASE}/filter` &&
      r.params.get('keyword') === 'abc' &&
      r.params.get('author') === 'ann' &&
      r.params.get('date') === '2024-01-01T00:00:00'
    );
    expect(req.request.method).toBe('GET');
    req.flush([P]);
  });

  it('getById(): GET /posts/:id (Promise)', async () => {
    const prom = service.getById(3);
    const req = http.expectOne(`${BASE}/3`);
    expect(req.request.method).toBe('GET');
    req.flush(P);

    const res = await prom;
    expect(res).toEqual(P);
  });

  it('getByIds(): GET /posts/by-ids?ids=1,2', (done) => {
    service.getByIds([1,2]).subscribe(res => {
      expect(res.length).toBe(2);
      done();
    });
    const req = http.expectOne(r => r.url === `${BASE}/by-ids` && r.params.get('ids') === '1,2');
    expect(req.request.method).toBe('GET');
    req.flush([P, { ...P, id: 2 }]);
  });

  it('getMySubmissions(): GET /posts/submissions met header username uit auth$', (done) => {
    service.getMySubmissions().subscribe(res => {
      expect(res.length).toBe(1);
      done();
    });

    const req = http.expectOne(`${BASE}/submissions`);
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.get('username')).toBe('ann');
    req.flush([P]);
  });


  it('gooit fout als niet ingelogd bij user-gebonden calls', async () => {
    TestBed.resetTestingModule();
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [{ provide: AuthService, useValue: mockAuth(null) }]
    });
    const s = TestBed.inject(PostService);

    await expectAsync(s.createDraft('x','y')).toBeRejectedWithError('Niet ingelogd');
    await expectAsync(s.getDrafts()).toBeRejectedWithError('Niet ingelogd');
    await expectAsync(s.submitDraft(1)).toBeRejectedWithError('Niet ingelogd');
    await expectAsync(s.updateDraft(1,'a','b')).toBeRejectedWithError('Niet ingelogd');
  });
});
