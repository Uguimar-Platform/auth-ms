import { Injectable } from '@nestjs/common';
import {
  CanActivate,
  ExecutionContext,
  HttpException,
  HttpStatus,
} from '@nestjs/common';
import { JwtService } from '@nestjs/jwt';

@Injectable()
export class JwtAuthGuard implements CanActivate {
  /**
   * The constructor is used to injectthe jwtService into the guard.
   *
   * @param jwtService The JwtService to use to verify the token.
   */
  constructor(private jwtService: JwtService) {}

  /**
   * Method that implements the logic to protect routes.
   * It is responsible for verifying the validity of the JWT token and assigning the decoded user to the request.
   *
   * @param context The execution context that contains information about the current request.
   * @returns True if the token is valid, or throws an HTTP exception if it is not.
   */

  async canActivate(context: ExecutionContext): Promise<boolean> {
    /**
     * We get the request from the context
     * and we extract the token from the authorization header
     * @param request The request object of the context
     * @param headers the request headers
     **/

    const request = context.switchToHttp().getRequest();
    const token = request.headers.authorization?.split(' ')[1];

    if (!token) {
      throw new HttpException('Unauthorized', HttpStatus.UNAUTHORIZED);
    }

    /**
     *
     * VWe verify the token with the verifyAsync method of the JwtService service
     * @param token The JWT token to be verified
     */

    try {
      const decoded = await this.jwtService.verifyAsync(token);
      request.user = decoded;
      return true;
    } catch (error) {
      if (error.name === 'TokenExpiredError') {
        throw new HttpException(
          {
            status: HttpStatus.UNAUTHORIZED,
            message: 'Token has expired',
          },
          HttpStatus.UNAUTHORIZED,
        );
      }
      throw new HttpException(
        {
          status: HttpStatus.UNAUTHORIZED,
          message: 'Unauthorized',
        },
        HttpStatus.UNAUTHORIZED,
      );
    }
  }
}
