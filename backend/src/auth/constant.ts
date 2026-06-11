import * as fs from 'fs';

export const jwtConstants = {
  secret: fs.readFileSync('/run/secrets/jwt_secret', 'utf8'),
};
