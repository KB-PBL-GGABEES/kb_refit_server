show databases;

show tables;

-- ***** DB 초기화 *****
-- 외래키 검사 OFF
SET FOREIGN_KEY_CHECKS = 0;

-- 테이블 삭제 (순서 상관 없이 모두 삭제 가능)
DROP TABLE IF EXISTS
    personalBadge, personWalletBrand, walletBrand, badge, receipt_process, hospital_process,
    insurance, reward, receiptContent, merchandise, receipt, company, categories, user;
-- 외래키 검사 ON
SET FOREIGN_KEY_CHECKS = 1;

-- ***** DB *****
-- user 유저 정보
CREATE TABLE user (
                      user_id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
                      username VARCHAR(255) NOT NULL,
                      password VARCHAR(255) NOT NULL,
                      role VARCHAR(50) NOT NULL,
                      refresh_token TEXT,
                      name VARCHAR(255) NOT NULL,
                      birth_date VARCHAR(20) NOT NULL,
                      total_carbon_point BIGINT NOT NULL,
                      total_star_point BIGINT NOT NULL,
                      created_at DATE NOT NULL,
                      updated_at DATE NOT NULL
);

INSERT INTO user (
    username, password, role, name, birth_date,
    total_carbon_point, total_star_point, created_at, updated_at
) VALUES (
             'admin', '$2a$10$FBBE8Dn1aJlA.pLEjbpyke9h0GlM8yvobq.xGVkdmC2DqLTG6zwqW',
             'ROLE_ADMIN', '조승연', '20020114', 1000, 2000, CURDATE(), CURDATE()
         );

-- categorise 카테고리
CREATE TABLE categories (
                            category_id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
                            category_name VARCHAR(50) NOT NULL
);

-- company 가게정보
CREATE TABLE company (
                         company_id BIGINT NOT NULL PRIMARY KEY,
                         ceo_id BIGINT NOT NULL,
                         company_name VARCHAR(50) NOT NULL,
                         ceo_name VARCHAR(20) NOT NULL,
                         address VARCHAR(50) NOT NULL,
                         opened_date DATE NOT NULL,
                         created_at DATE NOT NULL,
                         updated_at DATE NOT NULL,
                         category_id BIGINT NOT NULL,
                         FOREIGN KEY(category_id) REFERENCES categories(category_id)
);

-- receipt 전자 영수증
CREATE TABLE receipt (
                         receipt_id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
                         total_price BIGINT NOT NULL,
                         supply_price BIGINT NOT NULL,
                         surtax BIGINT NOT NULL,
                         transaction_type VARCHAR(255) NOT NULL,
                         created_at DATETIME NOT NULL,
                         updated_at DATETIME NOT NULL,
                         company_id BIGINT NOT NULL,
                         user_id BIGINT NOT NULL,
                         FOREIGN KEY(company_id) REFERENCES company(company_id),
                         FOREIGN KEY(user_id) REFERENCES user(user_id)
);

-- receiptProcess 영수처리
CREATE TABLE receipt_process (
                                 receipt_process_id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
                                 process_state VARCHAR(20) NOT NULL,
                                 ceo_id BIGINT NOT NULL,
                                 progress_type VARCHAR(255) DEFAULT NULL,
                                 progress_detail TEXT  DEFAULT NULL,
                                 created_at DATE NOT NULL,
                                 updated_at DATE DEFAULT NULL,
                                 rejected_reason TEXT  DEFAULT NULL,
                                 receipt_id BIGINT NOT NULL,
                                 FOREIGN KEY(receipt_id) REFERENCES receipt(receipt_id)
);

-- merchandise 상품
CREATE TABLE merchandise (
                             merchandise_id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
                             merchandise_name VARCHAR(255) NOT NULL,
                             merchandise_price BIGINT NOT NULL,
                             company_id BIGINT NOT NULL,
                             FOREIGN KEY(company_id) REFERENCES company(company_id)
);

-- receiptContent 구매내역
CREATE TABLE receiptContent (
                                receipt_content_id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
                                amount BIGINT NOT NULL,
                                created_at DATE NOT NULL,
                                receipt_id BIGINT NOT NULL,
                                merchandise_id BIGINT NOT NULL,
                                FOREIGN KEY(receipt_id) REFERENCES receipt(receipt_id),
                                FOREIGN KEY(merchandise_id) REFERENCES merchandise(merchandise_id)
);

-- insurance 보험
CREATE TABLE insurance (
                           insurance_id BIGINT AUTO_INCREMENT PRIMARY KEY AUTO_INCREMENT,
                           insurance_name VARCHAR(255) NOT NULL,
                           joined_date DATE NOT NULL,
                           user_id BIGINT NOT NULL,
                           FOREIGN KEY (user_id) REFERENCES user(user_id)
);

-- hospitalProcess 병원영수증 처리요청
CREATE TABLE hospital_process (
                                  hospital_process_id BIGINT AUTO_INCREMENT PRIMARY KEY AUTO_INCREMENT,
                                  process_state VARCHAR(20) NOT NULL DEFAULT 'none' CHECK (
                                      process_state IN ('accepted', 'rejected', 'inProgress', 'none')),
                                  sicked_date DATE DEFAULT NULL,
                                  visited_reason TEXT DEFAULT NULL,
                                  receipt_id BIGINT NOT NULL,
                                  insurance_id BIGINT NOT NULL,
                                  CONSTRAINT fk_hospital_receipt FOREIGN KEY (receipt_id) REFERENCES receipt(receipt_id) ON DELETE CASCADE,
                                  CONSTRAINT fk_hospital_insurance FOREIGN KEY (insurance_id) REFERENCES insurance(insurance_id) ON DELETE CASCADE
);

-- badge 뱃지
CREATE TABLE badge (
                       badge_id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
                       badge_image VARCHAR(255) NOT NULL,
                       badge_title VARCHAR(20) NOT NULL,
                       badge_benefit TEXT NOT NULL,
                       badge_condition TEXT NOT NULL,
                       category_id BIGINT NOT NULL,
                       FOREIGN KEY(category_id) REFERENCES categories(category_id)
);

-- personalBadge 보유뱃지
CREATE TABLE personalBadge (
                               personal_badge_id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
                               isWorn BOOLEAN DEFAULT FALSE,
                               created_at DATE NOT NULL,
                               updated_at DATE NOT NULL,
                               badge_id BIGINT NOT NULL,
                               user_id BIGINT NOT NULL,
                               FOREIGN KEY(badge_id) REFERENCES badge(badge_id),
                               FOREIGN KEY(user_id) REFERENCES user(user_id)
);

-- reward 리워드
CREATE TABLE reward (
                        reward_id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
                        carbon_point BIGINT NOT NULL,
                        reward BIGINT NOT NULL,
                        created_at DATE NOT NULL,
                        user_id BIGINT NOT NULL,
                        FOREIGN KEY(user_id) REFERENCES user(user_id)
);

-- walletBrand 지갑
CREATE TABLE WalletBrand (
                             wallet_id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
                             brand_image VARCHAR(255) NOT NULL,
                             brand_name VARCHAR(20) NOT NULL,
                             wallet_cost BIGINT NOT NULL,
                             brand_introduce TEXT NOT NULL
);

-- personWalletBrand 보유 지갑 브랜드
CREATE TABLE personWalletBrand (
                                   personWallet_brand_id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
                                   isMounted BOOLEAN DEFAULT FALSE,
                                   created_at DATE NOT NULL,
                                   wallet_id BIGINT NOT NULL,
                                   user_id BIGINT NOT NULL,
                                   FOREIGN KEY(wallet_id) REFERENCES walletBrand(wallet_id),
                                   FOREIGN KEY(user_id) REFERENCES user(user_id)
);

-- ****** 테스트용 데이터 ******
-- 카테고리
-- 카테고리
INSERT INTO categories (category_id, category_name)
VALUES
    (1, 'hospital'),
    (2, 'cafe'),
    (3, 'restaurant');


-- 스타벅스, 브네
INSERT INTO company (company_id, ceo_id, company_name, ceo_name, address, opened_date, created_at, updated_at, category_id)
VALUES
    (1, 1, '스타벅스', '김대표', '서울시 중구', '2022-01-01', CURDATE(), CURDATE(), 1),
    (2, 1, '브네', '김대표', '서울시 강남구', '2022-01-01', CURDATE(), CURDATE(), 1),
    (3, 1, '이디야커피', '이대표', '서울시 영등포구', '2023-03-01', CURDATE(), CURDATE(), 1),
    (4, 1, '투썸플레이스', '박대표', '서울시 마포구', '2024-01-15', CURDATE(), CURDATE(), 1),
    (5, 1, '컴포즈커피', '최대표', '서울시 성동구', '2022-09-12', CURDATE(), CURDATE(), 1);

INSERT INTO receipt (receipt_id, total_price, supply_price, surtax, transaction_type, created_at, updated_at, company_id, user_id)
VALUES
    (1, 5900, 5364, 536, 'CARD', '2025-07-13 14:10:00', NOW(), 1, 1),
    (2, 32500, 29545, 2955, 'CARD', '2025-07-09 19:59:00', NOW(), 2, 1),
    (3, 52500, 47727, 4773, 'CARD', '2025-07-09 19:59:00', NOW(), 2, 1),
    (4, 52500, 47727, 4773, 'CARD', '2025-07-09 15:10:00', NOW(), 2, 1),
    (5, 4100, 3727, 373, 'CARD', '2025-07-10 10:15:00', NOW(), 3, 1),
    (6, 13200, 12000, 1200, 'CARD', '2025-07-11 13:45:00', NOW(), 4, 1),
    (7, 2750, 2500, 250, 'CARD', '2025-07-12 09:30:00', NOW(), 5, 1);

-- 영수처리 내역
INSERT INTO receipt_process (
    receipt_process_id, process_state, ceo_id, progress_type,
    progress_detail, created_at, updated_at, rejected_reason, receipt_id
) VALUES
-- 상태: none
(1, 'none', 1, '업무 추진', '업무 추진 간 타사 협력을 위한 카페 방문', CURDATE(), CURDATE(), null, 1),
-- 상태: inProgress
(2, 'inProgress', 1, '진행중 예시', '테스트 내용', CURDATE(), CURDATE(), null, 2),
-- 상태: accepted
(3, 'accepted', 1, '경비처리 수락 예시', '테스트 내용', CURDATE(), CURDATE(), null, 3),
-- 상태: rejected
(4, 'rejected', 1, '경비처리 반려 예시', '테스트 내용', CURDATE(), CURDATE(), '지출 항목이 기준에 부적합', 4),
(5, 'none', 1, 'none 샘플 1', '테스트용 내용', CURDATE(), CURDATE(), null, 2),
(6, 'none', 1, 'none 샘플 2', '테스트용 내용', CURDATE(), CURDATE(), null, 3),
(7, 'none', 1, 'none 샘플 3', '테스트용 내용', CURDATE(), CURDATE(), null, 4);

-- 상품 내역
insert into merchandise (merchandise_name, merchandise_price, company_id) values ("카페 아메리카노", 4100, 1);
insert into merchandise (merchandise_name, merchandise_price, company_id) values ("카페 라떼", 4500, 1);
