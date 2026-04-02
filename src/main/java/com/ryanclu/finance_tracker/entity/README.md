BaseEntity — id, createdAt, lastModifiedAt (inherited by all)

User — standalone, no foreign keys, everything else points back to it

Account — belongs to User, carries a balance

Category — optionally belongs to User (null = system default)

Transaction — belongs to User, Account, and Category

Budget — belongs to User and Category, unique per user/category/period