package com.os.couchdbjs.client.model;


public class Base64 {

  public static final boolean ENCODE = true;
  public static final boolean DECODE = false;
  private static final byte EQUALS_SIGN = 61;
  private static final byte NEW_LINE = 10;
  public static final byte ALPHABET[] = {
    65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 
    75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 
    85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 
    101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 
    111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 
    121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 
    56, 57, 43, 47
  };
  public static final byte WEBSAFE_ALPHABET[] = {
    65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 
    75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 
    85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 
    101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 
    111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 
    121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 
    56, 57, 45, 95
  };
  private static final byte DECODABET[] = {
    -9, -9, -9, -9, -9, -9, -9, -9, -9, -5, 
    -5, -9, -9, -5, -9, -9, -9, -9, -9, -9, 
    -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 
    -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, 
    -9, -9, -9, 62, -9, -9, -9, 63, 52, 53, 
    54, 55, 56, 57, 58, 59, 60, 61, -9, -9, 
    -9, -1, -9, -9, -9, 0, 1, 2, 3, 4, 
    5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 
    15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 
    25, -9, -9, -9, -9, -9, -9, 26, 27, 28, 
    29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 
    39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 
    49, 50, 51, -9, -9, -9, -9, -9
  };
  private static final byte WEBSAFE_DECODABET[] = {
    -9, -9, -9, -9, -9, -9, -9, -9, -9, -5, 
    -5, -9, -9, -5, -9, -9, -9, -9, -9, -9, 
    -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 
    -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, 
    -9, -9, -9, -9, -9, 62, -9, -9, 52, 53, 
    54, 55, 56, 57, 58, 59, 60, 61, -9, -9, 
    -9, -1, -9, -9, -9, 0, 1, 2, 3, 4, 
    5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 
    15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 
    25, -9, -9, -9, -9, 63, -9, 26, 27, 28, 
    29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 
    39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 
    49, 50, 51, -9, -9, -9, -9, -9
  };
  private static final byte WHITE_SPACE_ENC = -5;
  private static final byte EQUALS_SIGN_ENC = -1;

  private Base64() {
  }

  private static byte[] encode3to4(byte source[], int srcOffset, int numSigBytes, byte destination[], int destOffset, byte alphabet[]) {
    int inBuff = (numSigBytes <= 0 ? 0 : (source[srcOffset] << 24) >>> 8) | (numSigBytes <= 1 ? 0 : (source[srcOffset + 1] << 24) >>> 16) | (numSigBytes <= 2 ? 0 : (source[srcOffset + 2] << 24) >>> 24);
    switch ( numSigBytes ) {
    case 3: // '\003'
      destination[destOffset] = alphabet[inBuff >>> 18];
      destination[destOffset + 1] = alphabet[inBuff >>> 12 & 0x3f];
      destination[destOffset + 2] = alphabet[inBuff >>> 6 & 0x3f];
      destination[destOffset + 3] = alphabet[inBuff & 0x3f];
      return (destination);
    case 2: // '\002'
      destination[destOffset] = alphabet[inBuff >>> 18];
      destination[destOffset + 1] = alphabet[inBuff >>> 12 & 0x3f];
      destination[destOffset + 2] = alphabet[inBuff >>> 6 & 0x3f];
      destination[destOffset + 3] = 61;
      return (destination);
    case 1: // '\001'
      destination[destOffset] = alphabet[inBuff >>> 18];
      destination[destOffset + 1] = alphabet[inBuff >>> 12 & 0x3f];
      destination[destOffset + 2] = 61;
      destination[destOffset + 3] = 61;
      return (destination);
    }
    return (destination);
  }

  public static java.lang.String encode(byte source[]) {
    return (encode(source, 0, source.length, ALPHABET, true));
  }

  public static String encodeWebSafe(byte source[], boolean doPadding) {
    return (encode(source, 0, source.length, WEBSAFE_ALPHABET, doPadding));
  }

  public static String encode(byte source[], int off, int len, byte alphabet[], boolean doPadding) {
    byte outBuff[] = encode(source, off, len, alphabet, 0x7fffffff);
    int outLen;
    for ( outLen = outBuff.length; !doPadding && outLen > 0 && outBuff[outLen - 1] == 61; outLen-- );
    StringBuilder sb = new StringBuilder(outLen);
    for(int i=0;i < outLen;i++) {
    	sb.append(outBuff[i]);
    }
    return sb.toString();
  }

  public static byte[] encode(byte source[], int off, int len, byte alphabet[], int maxLineLength) {
    int lenDiv3 = (len + 2) / 3;
    int len43 = lenDiv3 * 4;
    byte outBuff[] = new byte[len43 + len43 / maxLineLength];
    int d = 0;
    int e = 0;
    int len2 = len - 2;
    int lineLength = 0;
    while ( d < len2 ) {
      int inBuff = (source[d + off] << 24) >>> 8 | (source[d + 1 + off] << 24) >>> 16 | (source[d + 2 + off] << 24) >>> 24;
      outBuff[e] = alphabet[inBuff >>> 18];
      outBuff[e + 1] = alphabet[inBuff >>> 12 & 0x3f];
      outBuff[e + 2] = alphabet[inBuff >>> 6 & 0x3f];
      outBuff[e + 3] = alphabet[inBuff & 0x3f];
      if ( (lineLength += 4) == maxLineLength ) {
        outBuff[e + 4] = 10;
        e++;
        lineLength = 0;
      }
      d += 3;
      e += 4;
    }
    if ( d < len ) {
      encode3to4(source, d + off, len - d, outBuff, e, alphabet);
      if ( (lineLength += 4) == maxLineLength ) {
        outBuff[e + 4] = 10;
        e++;
      }
      e += 4;
    }
    return (outBuff);
  }

  private static int decode4to3(byte source[], int srcOffset, byte destination[], int destOffset, byte decodabet[]) {
    if ( source[srcOffset + 2] == 61 ) {
      int outBuff = (decodabet[source[srcOffset]] << 24) >>> 6 | (decodabet[source[srcOffset + 1]] << 24) >>> 12;
      destination[destOffset] = (byte)(outBuff >>> 16);
      return (1);
    }
    if ( source[srcOffset + 3] == 61 ) {
      int outBuff = (decodabet[source[srcOffset]] << 24) >>> 6 | (decodabet[source[srcOffset + 1]] << 24) >>> 12 | (decodabet[source[srcOffset + 2]] << 24) >>> 18;
      destination[destOffset] = (byte)(outBuff >>> 16);
      destination[destOffset + 1] = (byte)(outBuff >>> 8);
      return (2);
    } else {
      int outBuff = (decodabet[source[srcOffset]] << 24) >>> 6 | (decodabet[source[srcOffset + 1]] << 24) >>> 12 | (decodabet[source[srcOffset + 2]] << 24) >>> 18 | (decodabet[source[srcOffset + 3]] << 24) >>> 24;
      destination[destOffset] = (byte)(outBuff >> 16);
      destination[destOffset + 1] = (byte)(outBuff >> 8);
      destination[destOffset + 2] = (byte)outBuff;
      return (3);
    }
  }

  private static byte[] getBytes(String pStr) {
  	byte[] res = new byte[pStr.length()];
  	for(int i=0;i < res.length;i++) {
  		res[i] = (byte)pStr.charAt(i);
  	}
  	return res;
  }
  
  public static byte[] decode(java.lang.String s) throws Base64DecoderException {
    byte bytes[] = getBytes(s);
    return (decode(bytes, 0, bytes.length));
  }

  public static byte[] decodeWebSafe(java.lang.String s) throws Base64DecoderException {
    byte bytes[] = getBytes(s);
    return (decodeWebSafe(bytes, 0, bytes.length));
  }

  public static byte[] decode(byte source[]) throws Base64DecoderException {
    return (decode(source, 0, source.length));
  }

  public static byte[] decodeWebSafe(byte source[]) throws Base64DecoderException {
    return (decodeWebSafe(source, 0, source.length));
  }

  public static byte[] decode(byte source[], int off, int len) throws Base64DecoderException {
    return (decode(source, off, len, DECODABET));
  }

  public static byte[] decodeWebSafe(byte source[], int off, int len) throws Base64DecoderException {
    return (decode(source, off, len, WEBSAFE_DECODABET));
  }

  public static byte[] decode(byte source[], int off, int len, byte decodabet[]) throws Base64DecoderException {
    int len34 = (len * 3) / 4;
    byte outBuff[] = new byte[2 + len34];
    int outBuffPosn = 0;
    byte b4[] = new byte[4];
    int b4Posn = 0;
    int i = 0;
    byte sbiCrop = 0;
    byte sbiDecode = 0;
    for ( i = 0; i < len; i++ ) {
      sbiCrop = (byte)(source[i + off] & 0x7f);
      sbiDecode = decodabet[sbiCrop];
      if ( sbiDecode >= -5 ) {
        if ( sbiDecode < -1 )
          continue;
        if ( sbiCrop == 61 ) {
          int bytesLeft = len - i;
          byte lastByte = (byte)(source[(len - 1) + off] & 0x7f);
          if ( b4Posn == 0 || b4Posn == 1 )
            throw new Base64DecoderException((new StringBuilder()).append("invalid padding byte '=' at byte offset ").append(i).toString());
          if ( b4Posn == 3 && bytesLeft > 2 || b4Posn == 4 && bytesLeft > 1 )
            throw new Base64DecoderException((new StringBuilder()).append("padding byte '=' falsely signals end of encoded value at offset ").append(i).toString());
          if ( lastByte != 61 && lastByte != 10 )
            throw new Base64DecoderException("encoded value has invalid trailing byte");
          break;
        }
        b4[b4Posn++] = sbiCrop;
        if ( b4Posn == 4 ) {
          outBuffPosn += decode4to3(b4, 0, outBuff, outBuffPosn, decodabet);
          b4Posn = 0;
        }
      } else {
        throw new Base64DecoderException((new StringBuilder()).append("Bad Base64 input character at ").append(i).append(": ").append(source[i + off]).append("(decimal)").toString());
      }
    }

    if ( b4Posn != 0 ) {
      if ( b4Posn == 1 )
        throw new Base64DecoderException((new StringBuilder()).append("single trailing character at offset ").append(len - 1).toString());
      b4[b4Posn++] = 61;
      outBuffPosn += decode4to3(b4, 0, outBuff, outBuffPosn, decodabet);
    }
    byte out[] = new byte[outBuffPosn];
    java.lang.System.arraycopy(outBuff, 0, out, 0, outBuffPosn);
    return (out);
  }
 }